package com.example.tratohecho.data

import com.example.tratohecho.data.remote.IndicadoresApi
import com.example.tratohecho.data.remote.RetrofitInstance

class TratoHechoRepository(
    private val database: AppDatabase,
    private val indicadoresApi: IndicadoresApi = RetrofitInstance.indicadoresApi
) {
    val clientes = database.clienteDao().observarClientes()
    val cotizaciones = database.cotizacionDao().observarCotizaciones()

    suspend fun obtenerIndicadores() = indicadoresApi.obtenerIndicadores()

    suspend fun crearCliente(cliente: Cliente): Long {
        return database.clienteDao().insertar(cliente)
    }

    suspend fun obtenerCliente(id: Long): Cliente? {
        return database.clienteDao().obtenerCliente(id)
    }

    suspend fun actualizarCliente(cliente: Cliente) {
        database.clienteDao().actualizar(cliente)
    }

    suspend fun crearCotizacion(cotizacion: Cotizacion): Long {
        return database.cotizacionDao().insertar(cotizacion)
    }

    suspend fun cambiarEstadoCotizacion(id: Long, estado: String) {
        database.cotizacionDao().cambiarEstado(id, estado)
    }
}
