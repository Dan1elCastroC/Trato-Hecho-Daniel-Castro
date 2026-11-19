package com.example.tratohecho.data

import com.example.tratohecho.data.remote.ClienteDto
import com.example.tratohecho.data.remote.CotizacionDto
import com.example.tratohecho.data.remote.IndicadoresApi
import com.example.tratohecho.data.remote.LineaDto
import com.example.tratohecho.data.remote.RetrofitInstance
import com.example.tratohecho.data.remote.TratoHechoApi

/**
 * Repositorio: el microservicio Spring Boot (MySQL) es la fuente de verdad.
 * Room guarda una copia local para que las listas se vean rapido y sin conexion.
 * Cada operacion escribe primero en el microservicio y, si resulta bien, actualiza Room.
 */
class TratoHechoRepository(
    private val database: AppDatabase,
    private val indicadoresApi: IndicadoresApi = RetrofitInstance.indicadoresApi,
    private val api: TratoHechoApi = RetrofitInstance.tratoHechoApi
) {
    val clientes = database.clienteDao().observarClientes()
    val cotizaciones = database.cotizacionDao().observarCotizaciones()

    // ---- API externa (mindicador.cl) ----
    suspend fun obtenerIndicadores() = indicadoresApi.obtenerIndicadores()

    // ---- Sincronizacion: baja todo del microservicio y refresca Room ----
    suspend fun sincronizar() {
        val remotosClientes = api.listarClientes()
        val remotasCotizaciones = api.listarCotizaciones()
        val clienteDao = database.clienteDao()
        val cotizacionDao = database.cotizacionDao()

        clienteDao.upsertTodos(remotosClientes.map { dto ->
            // La foto solo existe en el celular: se conserva la local.
            val fotoLocal = dto.id?.let { clienteDao.obtenerCliente(it)?.fotoUri }.orEmpty()
            dto.aCliente(fotoLocal)
        })
        clienteDao.eliminarFueraDe(remotosClientes.mapNotNull { it.id })
        cotizacionDao.upsertTodas(remotasCotizaciones.map { it.aCotizacion() })
        cotizacionDao.eliminarFueraDe(remotasCotizaciones.mapNotNull { it.id })
    }

    // ---- Clientes (CRUD remoto + cache local) ----
    suspend fun crearCliente(cliente: Cliente): Long {
        val creado = api.crearCliente(cliente.aDto())
        val id = creado.id ?: error("El microservicio no devolvio id")
        database.clienteDao().insertar(cliente.copy(id = id))
        return id
    }

    suspend fun obtenerCliente(id: Long): Cliente? {
        return database.clienteDao().obtenerCliente(id)
    }

    suspend fun actualizarCliente(cliente: Cliente) {
        api.actualizarCliente(cliente.id, cliente.aDto())
        database.clienteDao().actualizar(cliente)
    }

    suspend fun eliminarCliente(id: Long) {
        api.eliminarCliente(id)
        database.clienteDao().eliminar(id) // las cotizaciones locales caen por CASCADE
    }

    // ---- Cotizaciones ----
    suspend fun crearCotizacion(cotizacion: Cotizacion): Long {
        val creada = api.crearCotizacion(cotizacion.aDto())
        val id = creada.id ?: error("El microservicio no devolvio id")
        database.cotizacionDao().insertar(cotizacion.copy(id = id))
        return id
    }

    suspend fun cambiarEstadoCotizacion(id: Long, estado: String) {
        api.cambiarEstado(id, estado)
        database.cotizacionDao().cambiarEstado(id, estado)
    }

    suspend fun eliminarCotizacion(id: Long) {
        api.eliminarCotizacion(id)
        database.cotizacionDao().eliminar(id)
    }
}

// ---- Conversiones entre el modelo de Room y los DTO del microservicio ----
fun Cliente.aDto() = ClienteDto(
    id = id.takeIf { it != 0L }, nombre = nombre, correo = correo, telefono = telefono,
    empresa = empresa, latitud = latitud, longitud = longitud
)

fun ClienteDto.aCliente(fotoUri: String = "") = Cliente(
    id = id ?: 0, nombre = nombre, correo = correo, telefono = telefono,
    empresa = empresa, fotoUri = fotoUri, latitud = latitud, longitud = longitud
)

fun Cotizacion.aDto() = CotizacionDto(
    id = id.takeIf { it != 0L }, clienteId = clienteId, moneda = moneda,
    descuentoPorcentaje = descuentoPorcentaje, impuestoPorcentaje = impuestoPorcentaje, estado = estado,
    lineas = obtenerLineas().map { LineaDto(it.concepto, it.cantidad, it.precioUnitario) }
)

fun CotizacionDto.aCotizacion(): Cotizacion {
    val lineasLocales = lineas.map { LineaCotizacion(it.concepto, it.cantidad, it.precioUnitario) }
    val primera = lineasLocales.firstOrNull()
    return Cotizacion(
        id = id ?: 0, clienteId = clienteId,
        concepto = primera?.concepto.orEmpty(), cantidad = primera?.cantidad ?: 1,
        precioUnitario = primera?.precioUnitario ?: 0.0,
        descuentoPorcentaje = descuentoPorcentaje, impuestoPorcentaje = impuestoPorcentaje,
        moneda = moneda, lineasJson = lineasLocales.aJson(), estado = estado
    )
}
