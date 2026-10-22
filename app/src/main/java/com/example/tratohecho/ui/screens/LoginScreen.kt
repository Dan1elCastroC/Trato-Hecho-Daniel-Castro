package com.example.tratohecho.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Login
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
fun LoginScreen(onLoginCorrecto: () -> Unit) {
    var usuario by remember { mutableStateOf("") }
    var clave by remember { mutableStateOf("") }

    var intentoIngresar by remember { mutableStateOf(false) }
    var usuarioEditado by remember { mutableStateOf(false) }
    var claveEditada by remember { mutableStateOf(false) }

    val errorUsuario = if (usuarioEditado || intentoIngresar) ValidadorCampos.validarNombre(usuario) else null
    val errorClave = if (claveEditada || intentoIngresar) ValidadorCampos.validarClave(clave) else null
    val formularioValido = listOf(errorUsuario, errorClave).all { it == null } &&
        usuario.isNotBlank() && clave.isNotBlank()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
            .animateContentSize(),
        verticalArrangement = Arrangement.Center
    ) {
        Text("Trato Hecho Mobile", style = MaterialTheme.typography.headlineLarge)
        Text("Cotizaciones móviles para vendedores en terreno")
        Spacer(Modifier.height(24.dp))

        CampoTexto(
            label = "Usuario",
            valor = usuario,
            onValorChange = {
                usuario = it
                usuarioEditado = true
            },
            error = errorUsuario,
            modifier = Modifier.fillMaxWidth()
        )
        CampoTexto(
            label = "Clave demostrativa",
            valor = clave,
            onValorChange = {
                clave = it
                claveEditada = true
            },
            error = errorClave,
            keyboardType = KeyboardType.Password,
            modifier = Modifier.fillMaxWidth()
        )

        AnimatedVisibility(visible = intentoIngresar && !formularioValido) {
            Text(
                text = "Corrige los campos marcados antes de ingresar.",
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall
            )
        }

        Spacer(Modifier.height(16.dp))
        Button(
            onClick = {
                intentoIngresar = true
                if (formularioValido) {
                    onLoginCorrecto()
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(Icons.Default.Login, contentDescription = null)
            Text("  Ingresar")
        }
    }
}
