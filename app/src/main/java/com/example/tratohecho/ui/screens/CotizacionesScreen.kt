package com.example.tratohecho.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.tratohecho.data.Cliente
import com.example.tratohecho.data.Cotizacion
import com.example.tratohecho.data.obtenerLineas
import com.example.tratohecho.ui.components.calcularTotal
import com.example.tratohecho.ui.components.dinero

@Composable
fun CotizacionesScreen(
    cotizaciones: List<Cotizacion>,
    clientes: List<Cliente>,
    onCambiarEstado: (Long, String) -> Unit,
    onVolverClientes: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text("Seguimiento de cotizaciones", style = MaterialTheme.typography.headlineMedium)
        Text("Permite cambiar el estado: enviada, aceptada, rechazada o facturada.")
        Spacer(Modifier.height(8.dp))
        OutlinedButton(onClick = onVolverClientes) {
            Text("Volver a clientes")
        }
        Spacer(Modifier.height(8.dp))

        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(cotizaciones, key = { it.id }) { cotizacion ->
                val cliente = clientes.find { it.id == cotizacion.clienteId }
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(16.dp)) {
                        Text(cotizacion.obtenerLineas().joinToString(", ") { it.concepto }, style = MaterialTheme.typography.titleMedium)
                        Text("Cliente: ${cliente?.nombre ?: "Sin cliente"}")
                        Text("Total: ${dinero(calcularTotal(cotizacion))} ${cotizacion.moneda}")
                        Text("Estado actual: ${cotizacion.estado}")
                        Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                            listOf("Enviada", "Aceptada", "Rechazada", "Facturada").forEach { estado ->
                                TextButton(onClick = { onCambiarEstado(cotizacion.id, estado) }) {
                                    Text(estado, style = MaterialTheme.typography.labelSmall)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
