package com.example.tratohecho.api.repository

import com.example.tratohecho.api.model.Cliente
import com.example.tratohecho.api.model.Cotizacion
import org.springframework.data.jpa.repository.JpaRepository

interface ClienteRepository : JpaRepository<Cliente, Long>

interface CotizacionRepository : JpaRepository<Cotizacion, Long> {
    fun findByClienteId(clienteId: Long): List<Cotizacion>
    fun deleteByClienteId(clienteId: Long)
}
