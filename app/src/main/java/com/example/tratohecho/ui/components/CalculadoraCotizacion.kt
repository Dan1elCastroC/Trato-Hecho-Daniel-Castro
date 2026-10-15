package com.example.tratohecho.ui.components

import com.example.tratohecho.data.Cotizacion
import java.util.Locale

fun calcularTotal(cotizacion: Cotizacion): Double {
    val subtotal = cotizacion.cantidad * cotizacion.precioUnitario
    val conDescuento = subtotal * (1 - cotizacion.descuentoPorcentaje / 100)
    return conDescuento * (1 + cotizacion.impuestoPorcentaje / 100)
}

fun dinero(valor: Double): String {
    return String.format(Locale("es", "CL"), "%,.0f", valor)
}
