package com.example.tratohecho.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier

private val sugerencias = listOf(
    "Producto", "Materiales", "Equipo", "Repuesto",
    "Servicio", "Instalación", "Mantención", "Reparación", "Asesoría", "Transporte"
)

/** Un campo editable con sugerencias desplegables, no limita la entrada libre. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CampoProductoServicio(
    valor: String,
    onValorChange: (String) -> Unit,
    error: String?,
    modifier: Modifier = Modifier
) {
    var expandido by remember { mutableStateOf(false) }
    val opciones = remember(valor) {
        sugerencias.filter { valor.isBlank() || it.contains(valor, ignoreCase = true) }
    }
    ExposedDropdownMenuBox(
        expanded = expandido && opciones.isNotEmpty(),
        onExpandedChange = { expandido = it },
        modifier = modifier
    ) {
        OutlinedTextField(
            value = valor,
            onValueChange = { nuevo ->
                onValorChange(nuevo)
                expandido = true
            },
            modifier = Modifier.menuAnchor().fillMaxWidth(),
            label = { Text("Producto o servicio") },
            placeholder = { Text("Selecciona una opción o escribe otra") },
            singleLine = true,
            isError = error != null,
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandido && opciones.isNotEmpty()) },
            supportingText = { if (error != null) Text(error) }
        )
        ExposedDropdownMenu(
            expanded = expandido && opciones.isNotEmpty(),
            onDismissRequest = { expandido = false }
        ) {
            opciones.forEach { opcion ->
                DropdownMenuItem(
                    text = { Text(opcion) },
                    onClick = {
                        onValorChange(opcion)
                        expandido = false
                    }
                )
            }
        }
    }
}
