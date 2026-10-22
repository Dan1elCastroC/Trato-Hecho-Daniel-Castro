package com.example.tratohecho.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.tratohecho.data.Cliente
import com.example.tratohecho.data.Cotizacion
import com.example.tratohecho.data.obtenerLineas
import com.example.tratohecho.ui.components.calcularTotal
import com.example.tratohecho.ui.components.dinero

@Composable
fun ResumenCotizacionScreen(
    cliente: Cliente?,
    cotizacion: Cotizacion?,
    onVerCotizaciones: () -> Unit,
    onVolverClientes: () -> Unit
) {
    var visible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        visible = true
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        AnimatedVisibility(visible = visible) {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp)) {
                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    Text("Cotización guardada", style = MaterialTheme.typography.headlineMedium)
                    Text("Cliente: ${cliente?.nombre ?: "Sin cliente"}")
                    if (cotizacion != null) {
                        val lineas = cotizacion.obtenerLineas()
                        val subtotal = lineas.sumOf { it.subtotal }
                        Text("Productos y servicios (${lineas.size}):", style = MaterialTheme.typography.titleSmall)
                        lineas.forEachIndexed { indice, linea ->
                            Text("${indice + 1}. ${linea.concepto} — ${linea.cantidad} × ${dinero(linea.precioUnitario)} ${cotizacion.moneda}")
                            Text("   Subtotal: ${dinero(linea.subtotal)} ${cotizacion.moneda}")
                        }
                        Text("Subtotal: ${dinero(subtotal)} ${cotizacion.moneda}")
                        Text("Descuento: ${cotizacion.descuentoPorcentaje}%")
                        Text("Impuesto: ${cotizacion.impuestoPorcentaje}%")
                        Text("Total: ${dinero(calcularTotal(cotizacion))} ${cotizacion.moneda}")
                        Text("Estado: ${cotizacion.estado}")
                    }
                }
            }
        }

        Spacer(Modifier.height(16.dp))
        Button(onClick = onVerCotizaciones, modifier = Modifier.fillMaxWidth()) {
            Text("Ver cotizaciones")
        }
        OutlinedButton(onClick = onVolverClientes, modifier = Modifier.fillMaxWidth()) {
            Text("Volver a la ficha del cliente")
        }
    }
}
