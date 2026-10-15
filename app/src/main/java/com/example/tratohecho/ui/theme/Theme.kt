package com.example.tratohecho.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColors = lightColorScheme(
    primary = VerdePrincipal,
    secondary = AzulSecundario,
    surfaceVariant = VerdeClaro
)

@Composable
fun TratoHechoTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = LightColors,
        typography = Typography,
        content = content
    )
}
