package com.example.tratohecho.model

import android.util.Patterns

object ValidadorCampos {
    private val nombreRegex = Regex("^[A-Za-zÁÉÍÓÚáéíóúÑñÜü .'-]+$")
    private val telefonoRegex = Regex("^[0-9]+$")
    private val conceptoRegex = Regex("^[A-Za-zÁÉÍÓÚáéíóúÑñÜü0-9 .,#/º°-]+$")
    private val decimalRegex = Regex("^[0-9]+([.,][0-9]+)?$")

    fun validarNombre(valor: String): String? {
        val texto = valor.trim()
        return when {
            texto.isEmpty() -> "Campo obligatorio"
            texto.length < 3 -> "Mínimo 3 caracteres"
            !nombreRegex.matches(texto) -> "Solo letras, espacios, punto, guion o apóstrofe"
            else -> null
        }
    }

    fun validarCorreo(valor: String): String? {
        val texto = valor.trim()
        return when {
            texto.isEmpty() -> "Campo obligatorio"
            texto.any { it.isWhitespace() } -> "El correo no debe tener espacios"
            !Patterns.EMAIL_ADDRESS.matcher(texto).matches() -> "Correo inválido. Ej: cliente@correo.cl"
            else -> null
        }
    }

    fun validarTelefono(valor: String): String? {
        val texto = valor.trim()
        return when {
            texto.isEmpty() -> "Campo obligatorio"
            !telefonoRegex.matches(texto) -> "Solo números, sin espacios ni símbolos"
            texto.length < 8 -> "Mínimo 8 dígitos"
            texto.length > 12 -> "Máximo 12 dígitos"
            else -> null
        }
    }

    fun validarClave(valor: String): String? {
        return when {
            valor.isBlank() -> "Campo obligatorio"
            valor.any { it.isWhitespace() } -> "La clave no debe tener espacios"
            valor.length < 4 -> "Mínimo 4 caracteres"
            else -> null
        }
    }

    fun validarConcepto(valor: String): String? {
        val texto = valor.trim()
        return when {
            texto.isEmpty() -> "Campo obligatorio"
            texto.length < 3 -> "Describe mejor el producto o servicio"
            !conceptoRegex.matches(texto) -> "Caracter no permitido en el concepto"
            else -> null
        }
    }

    fun validarCantidad(valor: String): String? {
        val texto = valor.trim()
        val numero = texto.toIntOrNull()
        return when {
            texto.isEmpty() -> "Campo obligatorio"
            !telefonoRegex.matches(texto) -> "Solo números enteros"
            numero == null || numero <= 0 -> "Cantidad mayor que cero"
            numero > 999 -> "Cantidad máxima: 999"
            else -> null
        }
    }

    fun validarPrecio(valor: String): String? {
        val texto = valor.trim()
        val numero = texto.replace(',', '.').toDoubleOrNull()
        return when {
            texto.isEmpty() -> "Campo obligatorio"
            !decimalRegex.matches(texto) -> "Solo números. Usa punto o coma decimal"
            numero == null || numero <= 0.0 -> "Precio mayor que cero"
            else -> null
        }
    }

    fun validarPorcentaje(valor: String): String? {
        val texto = valor.trim()
        val numero = texto.replace(',', '.').toDoubleOrNull()
        return when {
            texto.isEmpty() -> "Campo obligatorio"
            !decimalRegex.matches(texto) -> "Solo números. Usa punto o coma decimal"
            numero == null || numero !in 0.0..100.0 -> "Debe estar entre 0 y 100"
            else -> null
        }
    }

    fun aDouble(valor: String): Double = valor.trim().replace(',', '.').toDouble()
}
