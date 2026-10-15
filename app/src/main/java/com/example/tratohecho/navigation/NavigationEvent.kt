package com.example.tratohecho.navigation

sealed class NavigationEvent {
    data object VolverAClientes : NavigationEvent()
    data class IrAVisita(val clienteId: Long) : NavigationEvent()
    data class IrACotizacion(val clienteId: Long) : NavigationEvent()
    data class IrAResumen(val cotizacionId: Long) : NavigationEvent()
}
