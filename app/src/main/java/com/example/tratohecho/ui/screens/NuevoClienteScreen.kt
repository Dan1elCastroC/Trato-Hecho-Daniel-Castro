package com.example.tratohecho.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.Button
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
import com.example.tratohecho.model.ValidadorCampos
import com.example.tratohecho.ui.components.CampoTexto

@Composable
fun NuevoClienteScreen(onGuardar: (String, String, String) -> Unit) {
    var nombre by remember { mutableStateOf("") }
    var correo by remember { mutableStateOf("") }
    var telefono by remember { mutableStateOf("") }

    var intentoGuardar by remember { mutableStateOf(false) }
    var nombreEditado by remember { mutableStateOf(false) }
    var correoEditado by remember { mutableStateOf(false) }
    var telefonoEditado by remember { mutableStateOf(false) }

    val errorNombre = if (nombreEditado || intentoGuardar) ValidadorCampos.validarNombre(nombre) else null
    val errorCorreo = if (correoEditado || intentoGuardar) ValidadorCampos.validarCorreo(correo) else null
    val errorTelefono = if (telefonoEditado || intentoGuardar) ValidadorCampos.validarTelefono(telefono) else null
    val formularioValido = listOf(errorNombre, errorCorreo, errorTelefono).all { it == null } &&
        nombre.isNotBlank() && correo.isNotBlank() && telefono.isNotBlank()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .animateContentSize()
    ) {
        Text("Nuevo cliente", style = MaterialTheme.typography.headlineMedium)
        Text("Las validaciones aparecen en tiempo real al escribir datos no válidos.")
        Spacer(Modifier.height(12.dp))

        CampoTexto(
            label = "Nombre del cliente",
            valor = nombre,
            onValorChange = {
                nombre = it
                nombreEditado = true
            },
            error = errorNombre,
            modifier = Modifier.fillMaxWidth()
        )
        CampoTexto(
            label = "Correo",
            valor = correo,
            onValorChange = {
                correo = it
                correoEditado = true
            },
            error = errorCorreo,
            keyboardType = KeyboardType.Email,
            modifier = Modifier.fillMaxWidth()
        )
        CampoTexto(
            label = "Teléfono",
            valor = telefono,
            onValorChange = {
                telefono = it
                telefonoEditado = true
            },
            error = errorTelefono,
            keyboardType = KeyboardType.Phone,
            modifier = Modifier.fillMaxWidth()
        )

        AnimatedVisibility(visible = intentoGuardar && !formularioValido) {
            Text(
                text = "Revisa los datos del cliente. Hay campos inválidos.",
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall
            )
        }

        Spacer(Modifier.height(16.dp))
        Button(
            onClick = {
                intentoGuardar = true
                if (formularioValido) {
                    onGuardar(nombre.trim(), correo.trim(), telefono.trim())
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(Icons.Default.Save, contentDescription = null)
            Text("  Guardar cliente")
        }
    }
}
