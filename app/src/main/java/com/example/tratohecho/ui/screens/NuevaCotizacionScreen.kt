package com.example.tratohecho.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.tratohecho.data.Cliente
import com.example.tratohecho.data.Cotizacion
import com.example.tratohecho.data.LineaCotizacion
import com.example.tratohecho.data.aJson
import com.example.tratohecho.model.ValidadorCampos
import com.example.tratohecho.ui.components.CampoProductoServicio
import com.example.tratohecho.ui.components.CampoTexto
import com.example.tratohecho.ui.components.dinero

private data class LineaFormulario(
    val id: Long,
    val concepto: String = "",
    val cantidad: String = "1",
    val precio: String = "",
    val conceptoEditado: Boolean = false,
    val cantidadEditada: Boolean = false,
    val precioEditado: Boolean = false
)

@Composable
fun NuevaCotizacionScreen(
    cliente: Cliente?,
    onGuardar: (Cotizacion) -> Unit
) {
    val lineas = remember { mutableStateListOf(LineaFormulario(id = 0L)) }
    var siguienteId by remember { mutableLongStateOf(1L) }
    var descuento by remember { mutableStateOf("0") }
    var impuesto by remember { mutableStateOf("19") }
    var moneda by remember { mutableStateOf("CLP") }
    var intentoGuardar by remember { mutableStateOf(false) }
    var descuentoEditado by remember { mutableStateOf(false) }
    var impuestoEditado by remember { mutableStateOf(false) }

    fun actualizarLinea(id: Long, actualizar: (LineaFormulario) -> LineaFormulario) {
        val indice = lineas.indexOfFirst { it.id == id }
        if (indice >= 0) lineas[indice] = actualizar(lineas[indice])
    }

    val errorDescuento = if (descuentoEditado || intentoGuardar) ValidadorCampos.validarPorcentaje(descuento) else null
    val errorImpuesto = if (impuestoEditado || intentoGuardar) ValidadorCampos.validarPorcentaje(impuesto) else null
    val lineasValidas = lineas.isNotEmpty() && lineas.all {
        ValidadorCampos.validarConcepto(it.concepto) == null &&
            ValidadorCampos.validarCantidad(it.cantidad) == null &&
            ValidadorCampos.validarPrecio(it.precio) == null
    }
    val formularioValido = lineasValidas &&
        ValidadorCampos.validarPorcentaje(descuento) == null &&
        ValidadorCampos.validarPorcentaje(impuesto) == null
    val subtotal = lineas.sumOf { linea ->
        val cantidad = linea.cantidad.toIntOrNull() ?: 0
        val precio = linea.precio.replace(',', '.').toDoubleOrNull() ?: 0.0
        cantidad * precio
    }
    val descuentoNumero = descuento.replace(',', '.').toDoubleOrNull() ?: 0.0
    val impuestoNumero = impuesto.replace(',', '.').toDoubleOrNull() ?: 0.0
    val total = subtotal * (1 - descuentoNumero / 100) * (1 + impuestoNumero / 100)

    Column(
        modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Text("Nueva cotización", style = MaterialTheme.typography.headlineMedium)
        Text(cliente?.nombre ?: "Cliente no encontrado")

        lineas.forEachIndexed { indice, linea ->
            key(linea.id) {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Producto / servicio ${indice + 1}", style = MaterialTheme.typography.titleMedium)
                            IconButton(
                                onClick = { lineas.removeAll { it.id == linea.id } },
                                enabled = lineas.size > 1
                            ) {
                                Icon(Icons.Default.Delete, contentDescription = "Quitar producto o servicio ${indice + 1}")
                            }
                        }
                        CampoProductoServicio(
                            valor = linea.concepto,
                            onValorChange = { nuevo ->
                                actualizarLinea(linea.id) { it.copy(concepto = nuevo, conceptoEditado = true) }
                            },
                            error = if (linea.conceptoEditado || intentoGuardar)
                                ValidadorCampos.validarConcepto(linea.concepto) else null,
                            modifier = Modifier.fillMaxWidth()
                        )
                        CampoTexto(
                            label = "Cantidad",
                            valor = linea.cantidad,
                            onValorChange = { nuevo ->
                                actualizarLinea(linea.id) { it.copy(cantidad = nuevo, cantidadEditada = true) }
                            },
                            error = if (linea.cantidadEditada || intentoGuardar)
                                ValidadorCampos.validarCantidad(linea.cantidad) else null,
                            keyboardType = KeyboardType.Number,
                            modifier = Modifier.fillMaxWidth()
                        )
                        CampoTexto(
                            label = "Precio unitario",
                            valor = linea.precio,
                            onValorChange = { nuevo ->
                                actualizarLinea(linea.id) { it.copy(precio = nuevo, precioEditado = true) }
                            },
                            error = if (linea.precioEditado || intentoGuardar)
                                ValidadorCampos.validarPrecio(linea.precio) else null,
                            keyboardType = KeyboardType.Decimal,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        }

        OutlinedButton(
            onClick = { lineas.add(LineaFormulario(id = siguienteId++)) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(Icons.Default.Add, contentDescription = null)
            Text("  Agregar producto o servicio")
        }

        CampoTexto(
            label = "Descuento %",
            valor = descuento,
            onValorChange = { descuento = it; descuentoEditado = true },
            error = errorDescuento,
            keyboardType = KeyboardType.Decimal,
            modifier = Modifier.fillMaxWidth()
        )
        CampoTexto(
            label = "Impuesto %",
            valor = impuesto,
            onValorChange = { impuesto = it; impuestoEditado = true },
            error = errorImpuesto,
            keyboardType = KeyboardType.Decimal,
            modifier = Modifier.fillMaxWidth()
        )
        Text("Moneda")
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            listOf("CLP", "USD", "UF").forEach { opcion ->
                FilterChip(selected = moneda == opcion, onClick = { moneda = opcion }, label = { Text(opcion) })
            }
        }
        Text("USD y UF son monedas seleccionadas, sin conversión automática.", style = MaterialTheme.typography.bodySmall)
        Text("Subtotal: ${dinero(subtotal)} $moneda")
        Text("Total estimado: ${dinero(total)} $moneda", style = MaterialTheme.typography.titleMedium)

        AnimatedVisibility(visible = intentoGuardar && (!formularioValido || cliente == null)) {
            Text("Corrige los datos de los productos y porcentajes antes de guardar.",
                color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
        }
        Spacer(Modifier.height(8.dp))
        Button(
            onClick = {
                intentoGuardar = true
                if (formularioValido && cliente != null) {
                    val productos = lineas.map { linea ->
                        LineaCotizacion(
                            concepto = linea.concepto.trim(),
                            cantidad = linea.cantidad.toInt(),
                            precioUnitario = ValidadorCampos.aDouble(linea.precio)
                        )
                    }
                    val primero = productos.first()
                    onGuardar(
                        Cotizacion(
                            clienteId = cliente.id,
                            concepto = primero.concepto,
                            cantidad = primero.cantidad,
                            precioUnitario = primero.precioUnitario,
                            lineasJson = productos.aJson(),
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
