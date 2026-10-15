package com.example.tratohecho.ui.screens

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun HomeScreen(
    totalClientes: Int,
    totalCotizaciones: Int,
    onClientes: () -> Unit,
    onCotizaciones: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .animateContentSize(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Panel comercial", style = MaterialTheme.typography.headlineMedium)
        Text(
            "Flujo principal: registrar clientes, crear cotizaciones y hacer seguimiento. " +
                "La visita con cámara/GPS es una acción opcional en terreno."
        )

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Card(modifier = Modifier.weight(1f)) {
                Column(Modifier.padding(16.dp)) {
                    Icon(Icons.Default.Groups, contentDescription = null)
                    Text("Clientes", style = MaterialTheme.typography.titleMedium)
                    Text(totalClientes.toString(), style = MaterialTheme.typography.headlineSmall)
                }
            }
            Card(modifier = Modifier.weight(1f)) {
                Column(Modifier.padding(16.dp)) {
                    Icon(Icons.Default.Assignment, contentDescription = null)
                    Text("Cotizaciones", style = MaterialTheme.typography.titleMedium)
                    Text(totalCotizaciones.toString(), style = MaterialTheme.typography.headlineSmall)
                }
            }
        }

        Button(onClick = onClientes, modifier = Modifier.fillMaxWidth()) {
            Icon(Icons.Default.Groups, contentDescription = null)
            Text("  Gestionar clientes")
        }

        OutlinedButton(onClick = onCotizaciones, modifier = Modifier.fillMaxWidth()) {
            Icon(Icons.Default.Assignment, contentDescription = null)
            Text("  Seguimiento de cotizaciones")
        }

        Spacer(Modifier.height(4.dp))
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(Modifier.padding(16.dp)) {
                Icon(Icons.Default.LocationOn, contentDescription = null)
                Text("Recursos nativos", style = MaterialTheme.typography.titleMedium)
                Text("Cámara y GPS se usan dentro de Registrar visita, no como paso obligatorio después de crear un cliente.")
            }
        }
    }
}
