package com.example.tratohecho.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.Button
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.tratohecho.data.Cliente
import com.example.tratohecho.data.Cotizacion
import com.example.tratohecho.model.ValidadorCampos
import com.example.tratohecho.ui.components.CampoTexto

@Composable
fun NuevaCotizacionScreen(
    cliente: Cliente?,
    onGuardar: (Cotizacion) -> Unit
) {
    var concepto by remember { mutableStateOf("") }
    var cantidad by remember { mutableStateOf("1") }
    var precio by remember { mutableStateOf("") }
    var descuento by remember { mutableStateOf("0") }
    var impuesto by remember { mutableStateOf("19") }
    var moneda by remember { mutableStateOf("CLP") }

    var intentoGuardar by remember { mutableStateOf(false) }
    var conceptoEditado by remember { mutableStateOf(false) }
    var cantidadEditada by remember { mutableStateOf(false) }
    var precioEditado by remember { mutableStateOf(false) }
    var descuentoEditado by remember { mutableStateOf(false) }
    var impuestoEditado by remember { mutableStateOf(false) }

    val errorConcepto = if (conceptoEditado || intentoGuardar) ValidadorCampos.validarConcepto(concepto) else null
    val errorCantidad = if (cantidadEditada || intentoGuardar) ValidadorCampos.validarCantidad(cantidad) else null
    val errorPrecio = if (precioEditado || intentoGuardar) ValidadorCampos.validarPrecio(precio) else null
    val errorDescuento = if (descuentoEditado || intentoGuardar) ValidadorCampos.validarPorcentaje(descuento) else null
    val errorImpuesto = if (impuestoEditado || intentoGuardar) ValidadorCampos.validarPorcentaje(impuesto) else null
    val formularioValido = listOf(errorConcepto, errorCantidad, errorPrecio, errorDescuento, errorImpuesto).all { it == null } &&
        concepto.isNotBlank() && cantidad.isNotBlank() && precio.isNotBlank() && descuento.isNotBlank() && impuesto.isNotBlank()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .animateContentSize()
    ) {
        Text("Nueva cotización", style = MaterialTheme.typography.headlineMedium)
        Text(cliente?.nombre ?: "Cliente no encontrado")
        Spacer(Modifier.height(12.dp))

        CampoTexto(
            label = "Producto o servicio",
            valor = concepto,
            onValorChange = {
                concepto = it
                conceptoEditado = true
            },
            modifier = Modifier.fillMaxWidth(),
            error = errorConcepto
        )
        CampoTexto(
            label = "Cantidad",
            valor = cantidad,
            onValorChange = {
                cantidad = it
                cantidadEditada = true
            },
            modifier = Modifier.fillMaxWidth(),
            error = errorCantidad,
            keyboardType = KeyboardType.Number
        )
        CampoTexto(
            label = "Precio unitario",
            valor = precio,
            onValorChange = {
                precio = it
                precioEditado = true
            },
            modifier = Modifier.fillMaxWidth(),
            error = errorPrecio,
            keyboardType = KeyboardType.Number
        )
        CampoTexto(
            label = "Descuento %",
            valor = descuento,
            onValorChange = {
                descuento = it
                descuentoEditado = true
            },
            modifier = Modifier.fillMaxWidth(),
            error = errorDescuento,
            keyboardType = KeyboardType.Number
        )
        CampoTexto(
            label = "Impuesto %",
            valor = impuesto,
            onValorChange = {
                impuesto = it
                impuestoEditado = true
            },
            modifier = Modifier.fillMaxWidth(),
            error = errorImpuesto,
            keyboardType = KeyboardType.Number
        )

        Text("Moneda")
        Row {
            listOf("CLP", "USD", "UF").forEach { opcion ->
                FilterChip(
                    selected = moneda == opcion,
                    onClick = { moneda = opcion },
                    label = { Text(opcion) },
                    modifier = Modifier.padding(end = 8.dp)
                )
            }
        }
        Text(
            "En esta versión de avance, USD y UF quedan como moneda seleccionada; la API externa se deja para mejora posterior.",
            style = MaterialTheme.typography.bodySmall
        )

        AnimatedVisibility(visible = intentoGuardar && !formularioValido) {
            Text(
                text = "No se puede guardar: corrige los campos marcados.",
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall
            )
        }

        Spacer(Modifier.height(16.dp))
        Button(
            onClick = {
                intentoGuardar = true
                if (formularioValido && cliente != null) {
                    onGuardar(
                        Cotizacion(
                            clienteId = cliente.id,
                            concepto = concepto.trim(),
                            cantidad = cantidad.trim().toInt(),
                            precioUnitario = ValidadorCampos.aDouble(precio),
                            descuentoPorcentaje = ValidadorCampos.aDouble(descuento),
                            impuestoPorcentaje = ValidadorCampos.aDouble(impuesto),
                            moneda = moneda
                        )
                    )
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(Icons.Default.Save, contentDescription = null)
            Text("  Guardar cotización")
        }
    }
}
