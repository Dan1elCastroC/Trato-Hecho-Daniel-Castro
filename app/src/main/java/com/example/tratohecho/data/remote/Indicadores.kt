package com.example.tratohecho.data.remote

// Respuesta de https://mindicador.cl/api (solo los campos que usamos).
data class Indicador(val valor: Double?)

data class Indicadores(
    val uf: Indicador?,
    val dolar: Indicador?
)
