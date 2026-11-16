package com.example.tratohecho.ui.components

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import org.junit.Rule
import org.junit.Test

class CampoTextoTest {

    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun muestra_el_mensaje_de_error_cuando_el_dato_es_invalido() {
        composeRule.setContent {
            CampoTexto(label = "Teléfono", valor = "abc", onValorChange = {}, error = "Solo números")
        }
        composeRule.onNodeWithText("Solo números").assertIsDisplayed()
    }

    @Test
    fun no_muestra_error_cuando_el_dato_es_valido() {
        composeRule.setContent {
            CampoTexto(label = "Teléfono", valor = "912345678", onValorChange = {}, error = null)
        }
        composeRule.onNodeWithText("Teléfono").assertIsDisplayed()
    }
}
