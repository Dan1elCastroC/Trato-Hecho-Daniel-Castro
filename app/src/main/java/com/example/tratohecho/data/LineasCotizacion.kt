package com.example.tratohecho.data

import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

/** Cada cotización puede tener tantos productos o servicios como necesite. */
data class LineaCotizacion(
    val concepto: String,
    val cantidad: Int,
    val precioUnitario: Double
) {
    val subtotal: Double get() = cantidad * precioUnitario
}

/** Mantiene compatibilidad con las cotizaciones antiguas (que tenían solo una línea). */
fun Cotizacion.obtenerLineas(): List<LineaCotizacion> {
    val lineaAnterior = LineaCotizacion(concepto, cantidad, precioUnitario)
    if (lineasJson.isBlank()) return listOf(lineaAnterior)
    return try {
        val array = JSONArray(lineasJson)
        (0 until array.length()).map { indice ->
            val item = array.getJSONObject(indice)
            LineaCotizacion(
                concepto = item.getString("concepto"),
                cantidad = item.getInt("cantidad"),
                precioUnitario = item.getDouble("precioUnitario")
            )
        }.ifEmpty { listOf(lineaAnterior) }
    } catch (_: JSONException) {
        listOf(lineaAnterior)
    }
}

fun List<LineaCotizacion>.aJson(): String {
    val array = JSONArray()
    forEach { linea ->
        array.put(
            JSONObject().put("concepto", linea.concepto)
                .put("cantidad", linea.cantidad)
                .put("precioUnitario", linea.precioUnitario)
        )
    }
    return array.toString()
}
