package com.example.tratohecho.api.service

import com.example.tratohecho.api.dto.*
import com.example.tratohecho.api.model.*
import com.example.tratohecho.api.repository.ClienteRepository
import com.example.tratohecho.api.repository.CotizacionRepository
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.server.ResponseStatusException

@Service
class ClienteService(private val repo: ClienteRepository, private val cotizaciones: CotizacionRepository) {
    fun listar(): List<Cliente> = repo.findAll()

    fun obtener(id: Long): Cliente =
        repo.findById(id).orElseThrow { ResponseStatusException(HttpStatus.NOT_FOUND, "Cliente $id no existe") }

    fun crear(r: ClienteRequest): Cliente = repo.save(aEntidad(Cliente(), r))

    fun actualizar(id: Long, r: ClienteRequest): Cliente = repo.save(aEntidad(obtener(id), r))

    @Transactional
    fun eliminar(id: Long) {
        obtener(id)
        cotizaciones.deleteByClienteId(id)
        repo.deleteById(id)
    }

    private fun aEntidad(c: Cliente, r: ClienteRequest) = c.apply {
        nombre = r.nombre; correo = r.correo; telefono = r.telefono
        empresa = r.empresa; latitud = r.latitud; longitud = r.longitud
    }
}

@Service
class CotizacionService(private val repo: CotizacionRepository, private val clientes: ClienteRepository) {
    fun listar(clienteId: Long? = null): List<CotizacionResponse> =
        (if (clienteId == null) repo.findAll() else repo.findByClienteId(clienteId)).map { aRespuesta(it) }

    fun obtener(id: Long): CotizacionResponse = aRespuesta(buscar(id))

    fun crear(r: CotizacionRequest): CotizacionResponse {
        validarCliente(r.clienteId)
        return aRespuesta(repo.save(aEntidad(Cotizacion(), r)))
    }

    fun actualizar(id: Long, r: CotizacionRequest): CotizacionResponse {
        validarCliente(r.clienteId)
        return aRespuesta(repo.save(aEntidad(buscar(id), r)))
    }

    fun cambiarEstado(id: Long, estado: String): CotizacionResponse {
        val c = buscar(id)
        c.estado = estado
        return aRespuesta(repo.save(c))
    }

    fun eliminar(id: Long) {
        buscar(id)
        repo.deleteById(id)
    }

    fun calcularTotal(lineas: List<LineaCotizacion>, descuento: Double, impuesto: Double): Double {
        val subtotal = lineas.sumOf { it.cantidad * it.precioUnitario }
        val conDescuento = subtotal * (1 - descuento / 100)
        return conDescuento * (1 + impuesto / 100)
    }

    private fun buscar(id: Long) =
        repo.findById(id).orElseThrow { ResponseStatusException(HttpStatus.NOT_FOUND, "Cotizacion $id no existe") }

    private fun validarCliente(id: Long) {
        if (!clientes.existsById(id)) throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Cliente $id no existe")
    }

    private fun aEntidad(c: Cotizacion, r: CotizacionRequest) = c.apply {
        clienteId = r.clienteId; moneda = r.moneda
        descuentoPorcentaje = r.descuentoPorcentaje; impuestoPorcentaje = r.impuestoPorcentaje
        estado = r.estado
        lineas = r.lineas.map { LineaCotizacion(it.concepto, it.cantidad, it.precioUnitario) }.toMutableList()
    }

    private fun aRespuesta(c: Cotizacion) = CotizacionResponse(
        c.id, c.clienteId, c.moneda, c.descuentoPorcentaje, c.impuestoPorcentaje, c.estado,
        c.lineas.map { LineaDto(it.concepto, it.cantidad, it.precioUnitario) },
        calcularTotal(c.lineas, c.descuentoPorcentaje, c.impuestoPorcentaje)
    )
}
