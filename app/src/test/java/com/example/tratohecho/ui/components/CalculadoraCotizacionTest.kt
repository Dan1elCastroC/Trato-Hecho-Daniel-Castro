package com.example.tratohecho.ui.components

import com.example.tratohecho.data.Cotizacion
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.doubles.plusOrMinus
import io.kotest.matchers.shouldBe

class CalculadoraCotizacionTest : StringSpec({

    // lineasJson vacio => usa la linea unica (concepto, cantidad, precioUnitario)
    fun cotizacion(cantidad: Int, precio: Double, descuento: Double, impuesto: Double) = Cotizacion(
        clienteId = 1,
        concepto = "Servicio",
        cantidad = cantidad,
        precioUnitario = precio,
        descuentoPorcentaje = descuento,
        impuestoPorcentaje = impuesto,
        moneda = "CLP"
    )

    "total sin descuento con IVA 19%" {
        // 2 x 10.000 = 20.000 + 19% = 23.800
        calcularTotal(cotizacion(2, 10_000.0, 0.0, 19.0)) shouldBe (23_800.0 plusOrMinus 0.001)
    }

    "el descuento se aplica antes del impuesto" {
        // 100.000 - 10% = 90.000 + 19% = 107.100
        calcularTotal(cotizacion(1, 100_000.0, 10.0, 19.0)) shouldBe (107_100.0 plusOrMinus 0.001)
    }

    "sin descuento ni impuesto el total es el subtotal" {
        calcularTotal(cotizacion(3, 5_000.0, 0.0, 0.0)) shouldBe (15_000.0 plusOrMinus 0.001)
    }
})
