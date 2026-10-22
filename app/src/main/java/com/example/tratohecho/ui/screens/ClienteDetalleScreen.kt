package com.example.tratohecho.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.tratohecho.data.Cliente
import com.example.tratohecho.data.Cotizacion
import com.example.tratohecho.data.obtenerLineas
import com.example.tratohecho.ui.components.calcularTotal
import com.example.tratohecho.ui.components.dinero

@Composable
fun ClienteDetalleScreen(
    cliente: Cliente?,
    cotizaciones: List<Cotizacion>,
    onNuevaCotizacion: () -> Unit,
    onRegistrarVisita: () -> Unit,
    onVerCotizaciones: () -> Unit,
    onVolverClientes: () -> Unit
) {
    if (cliente == null) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text("Cliente no encontrado", style = MaterialTheme.typography.headlineMedium)
            Text("Vuelve a la lista de clientes y selecciona uno válido.")
            Button(onClick = onVolverClientes, modifier = Modifier.fillMaxWidth()) {
                Text("Volver a clientes")
            }
        }
        return
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .animateContentSize(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Ficha del cliente", style = MaterialTheme.typography.headlineMedium)
        Text("Desde aquí eliges si quieres cotizar, registrar visita o revisar seguimiento.")

        Card(modifier = Modifier.fillMaxWidth()) {
            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Icon(Icons.Default.Person, contentDescription = null)
                    Text(cliente.nombre, style = MaterialTheme.typography.titleLarge)
                }
                Text("Correo: ${cliente.correo}")
                Text("Teléfono: ${cliente.telefono}")
                Text("Empresa: ${cliente.empresa}")

                AnimatedVisibility(visible = cliente.latitud != null && cliente.longitud != null) {
                    Column {
                        Text("Última visita registrada:", style = MaterialTheme.typography.titleSmall)
                        Text("GPS: ${cliente.latitud}, ${cliente.longitud}")
                    }
                }

                AnimatedVisibility(visible = cliente.fotoUri.isNotBlank()) {
                    AsyncImage(
                        model = cliente.fotoUri,
                        contentDescription = "Foto de visita",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(150.dp)
                            .clip(RoundedCornerShape(16.dp)),
                        contentScale = ContentScale.Crop
                    )
                }
            }
        }

        Button(onClick = onNuevaCotizacion, modifier = Modifier.fillMaxWidth()) {
            Icon(Icons.Default.Assignment, contentDescription = null)
            Text("  Nueva cotización")
        }
        OutlinedButton(onClick = onRegistrarVisita, modifier = Modifier.fillMaxWidth()) {
            Icon(Icons.Default.CameraAlt, contentDescription = null)
            Text("  Registrar visita con cámara/GPS")
        }
        OutlinedButton(onClick = onVerCotizaciones, modifier = Modifier.fillMaxWidth()) {
            Icon(Icons.Default.LocationOn, contentDescription = null)
            Text("  Ver seguimiento")
        }

        Text("Cotizaciones del cliente", style = MaterialTheme.typography.titleMedium)
        AnimatedVisibility(visible = cotizaciones.isEmpty()) {
            Text("Aún no hay cotizaciones para este cliente.")
        }
        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(cotizaciones, key = { it.id }) { cotizacion ->
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(12.dp)) {
                        Text(cotizacion.obtenerLineas().joinToString(", ") { it.concepto }, style = MaterialTheme.typography.titleSmall)
                        Text("Total: ${dinero(calcularTotal(cotizacion))} ${cotizacion.moneda}")
                        Text("Estado: ${cotizacion.estado}")
                    }
                }
            }
        }
    }
}
