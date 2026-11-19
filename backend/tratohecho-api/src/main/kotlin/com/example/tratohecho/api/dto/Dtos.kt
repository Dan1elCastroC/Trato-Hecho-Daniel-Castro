package com.example.tratohecho.api.dto

import jakarta.validation.Valid
import jakarta.validation.constraints.*

data class ClienteRequest(
    @field:NotBlank(message = "El nombre es obligatorio") val nombre: String,
    @field:Email(message = "Correo invalido") @field:NotBlank(message = "El correo es obligatorio") val correo: String,
    @field:NotBlank(message = "El telefono es obligatorio") val telefono: String,
    val empresa: String = "Empresa demo",
    val latitud: Double? = null,
    val longitud: Double? = null
)

data class LineaDto(
    @field:NotBlank(message = "El concepto es obligatorio") val concepto: String,
    @field:Min(1, message = "La cantidad minima es 1") val cantidad: Int,
    @field:PositiveOrZero(message = "El precio no puede ser negativo") val precioUnitario: Double
)

data class CotizacionRequest(
    @field:Positive(message = "clienteId invalido") val clienteId: Long,
    val moneda: String = "CLP",
    @field:DecimalMin("0.0") @field:DecimalMax("100.0") val descuentoPorcentaje: Double = 0.0,
    @field:DecimalMin("0.0") @field:DecimalMax("100.0") val impuestoPorcentaje: Double = 19.0,
    val estado: String = "Borrador",
    @field:NotEmpty(message = "Debe tener al menos una linea") @field:Valid val lineas: List<LineaDto>
)

data class CotizacionResponse(
    val id: Long,
    val clienteId: Long,
    val moneda: String,
    val descuentoPorcentaje: Double,
    val impuestoPorcentaje: Double,
    val estado: String,
    val lineas: List<LineaDto>,
    val total: Double
)
