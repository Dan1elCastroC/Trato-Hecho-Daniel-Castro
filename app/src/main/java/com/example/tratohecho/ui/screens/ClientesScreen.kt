package com.example.tratohecho.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Card
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.tratohecho.data.Cliente

@Composable
fun ClientesScreen(
    clientes: List<Cliente>,
    onNuevoCliente: () -> Unit,
    onClienteSeleccionado: (Long) -> Unit,
    onVerCotizaciones: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text("Clientes", style = MaterialTheme.typography.headlineMedium)
        Text("Selecciona un cliente para cotizar, registrar visita o revisar su seguimiento.")
        Spacer(Modifier.height(12.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedButton(onClick = onNuevoCliente) {
                Icon(Icons.Default.Add, contentDescription = null)
                Text(" Cliente")
            }
            OutlinedButton(onClick = onVerCotizaciones) {
                Icon(Icons.Default.Assignment, contentDescription = null)
                Text(" Cotizaciones")
            }
        }

        Spacer(Modifier.height(12.dp))

        AnimatedVisibility(visible = clientes.isEmpty()) {
            Text("Aún no hay clientes. Crea uno para iniciar el flujo comercial.")
        }

        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(clientes, key = { it.id }) { cliente ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .animateContentSize()
                        .clickable { onClienteSeleccionado(cliente.id) }
                ) {
                    Column(Modifier.padding(16.dp)) {
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Icon(Icons.Default.Person, contentDescription = null)
                            Text(cliente.nombre, style = MaterialTheme.typography.titleMedium)
                        }
                        Text(cliente.correo)
                        Text(cliente.telefono)
                        if (cliente.latitud != null && cliente.longitud != null) {
                            Text("Última visita GPS: ${cliente.latitud}, ${cliente.longitud}")
                        }
                        TextButton(onClick = { onClienteSeleccionado(cliente.id) }) {
                            Text("Abrir ficha")
                        }
                    }
                }
            }
        }
    }

    ExtendedFloatingActionButton(
        onClick = onNuevoCliente,
        icon = { Icon(Icons.Default.Add, contentDescription = null) },
        text = { Text("Nuevo cliente") },
        modifier = Modifier.padding(16.dp)
    )
}
