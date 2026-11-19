package com.example.tratohecho.data.remote

import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

// ---- DTOs que viajan al microservicio Spring Boot (mismos nombres que el JSON del backend) ----
data class ClienteDto(
    val id: Long? = null,
    val nombre: String,
    val correo: String,
    val telefono: String,
    val empresa: String = "Empresa demo",
    val latitud: Double? = null,
    val longitud: Double? = null
)

data class LineaDto(val concepto: String, val cantidad: Int, val precioUnitario: Double)

data class CotizacionDto(
    val id: Long? = null,
    val clienteId: Long,
    val moneda: String,
    val descuentoPorcentaje: Double,
    val impuestoPorcentaje: Double,
    val estado: String,
    val lineas: List<LineaDto>,
    val total: Double? = null
)

/** RetrofitService del microservicio propio (CRUD de clientes y cotizaciones). */
interface TratoHechoApi {
    @GET("api/clientes") suspend fun listarClientes(): List<ClienteDto>
    @GET("api/clientes/{id}") suspend fun obtenerCliente(@Path("id") id: Long): ClienteDto
    @POST("api/clientes") suspend fun crearCliente(@Body cliente: ClienteDto): ClienteDto
    @PUT("api/clientes/{id}") suspend fun actualizarCliente(@Path("id") id: Long, @Body cliente: ClienteDto): ClienteDto
    @DELETE("api/clientes/{id}") suspend fun eliminarCliente(@Path("id") id: Long)

    @GET("api/cotizaciones") suspend fun listarCotizaciones(): List<CotizacionDto>
    @POST("api/cotizaciones") suspend fun crearCotizacion(@Body cotizacion: CotizacionDto): CotizacionDto
    @PUT("api/cotizaciones/{id}") suspend fun actualizarCotizacion(@Path("id") id: Long, @Body cotizacion: CotizacionDto): CotizacionDto
    @PATCH("api/cotizaciones/{id}/estado") suspend fun cambiarEstado(@Path("id") id: Long, @Query("estado") estado: String): CotizacionDto
    @DELETE("api/cotizaciones/{id}") suspend fun eliminarCotizacion(@Path("id") id: Long)
}
