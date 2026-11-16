package com.example.tratohecho.model

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

// Nota: validarCorreo usa android.util.Patterns, por eso no se prueba en JVM puro.
class ValidadorCamposTest : StringSpec({

    "nombre vacio es obligatorio" {
        ValidadorCampos.validarNombre("   ") shouldBe "Campo obligatorio"
    }

    "nombre con menos de 3 letras se rechaza" {
        ValidadorCampos.validarNombre("Al") shouldBe "Mínimo 3 caracteres"
    }

    "nombre con numeros se rechaza" {
        ValidadorCampos.validarNombre("Ana123") shouldBe
            "Solo letras, espacios, punto, guion o apóstrofe"
    }

    "nombre valido no devuelve error" {
        ValidadorCampos.validarNombre("María José Pérez") shouldBe null
    }

    "telefono con letras se rechaza" {
        ValidadorCampos.validarTelefono("9123abc45") shouldBe "Solo números, sin espacios ni símbolos"
    }

    "telefono corto y largo se rechazan" {
        ValidadorCampos.validarTelefono("1234567") shouldBe "Mínimo 8 dígitos"
        ValidadorCampos.validarTelefono("1234567890123") shouldBe "Máximo 12 dígitos"
    }

    "telefono valido no devuelve error" {
        ValidadorCampos.validarTelefono("912345678") shouldBe null
    }

    "clave con espacios o corta se rechaza" {
        ValidadorCampos.validarClave("ab cd") shouldBe "La clave no debe tener espacios"
        ValidadorCampos.validarClave("abc") shouldBe "Mínimo 4 caracteres"
        ValidadorCampos.validarClave("abcd") shouldBe null
    }

    "cantidad debe ser entera y entre 1 y 999" {
        ValidadorCampos.validarCantidad("0") shouldBe "Cantidad mayor que cero"
        ValidadorCampos.validarCantidad("1000") shouldBe "Cantidad máxima: 999"
        ValidadorCampos.validarCantidad("1,5") shouldBe "Solo números enteros"
        ValidadorCampos.validarCantidad("12") shouldBe null
    }

    "precio acepta punto o coma y rechaza cero" {
        ValidadorCampos.validarPrecio("1500,50") shouldBe null
        ValidadorCampos.validarPrecio("1500.50") shouldBe null
        ValidadorCampos.validarPrecio("0") shouldBe "Precio mayor que cero"
        ValidadorCampos.validarPrecio("abc") shouldBe "Solo números. Usa punto o coma decimal"
    }

    "porcentaje debe estar entre 0 y 100" {
        ValidadorCampos.validarPorcentaje("0") shouldBe null
        ValidadorCampos.validarPorcentaje("100") shouldBe null
        ValidadorCampos.validarPorcentaje("101") shouldBe "Debe estar entre 0 y 100"
    }

    "aDouble convierte coma decimal" {
        ValidadorCampos.aDouble(" 19,5 ") shouldBe 19.5
    }
})
