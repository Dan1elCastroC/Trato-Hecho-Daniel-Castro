package com.example.tratohecho.navigation

sealed class Screen(val route: String) {
    data object Login : Screen("login")
    data object Home : Screen("home")
    data object Clientes : Screen("clientes")
    data object NuevoCliente : Screen("nuevo_cliente")
    data object DetalleCliente : Screen("cliente_detalle/{clienteId}") {
        fun crearRuta(clienteId: Long) = "cliente_detalle/$clienteId"
    }
    data object Visita : Screen("visita/{clienteId}") {
        fun crearRuta(clienteId: Long) = "visita/$clienteId"
    }
    data object NuevaCotizacion : Screen("nueva_cotizacion/{clienteId}") {
        fun crearRuta(clienteId: Long) = "nueva_cotizacion/$clienteId"
    }
    data object ResumenCotizacion : Screen("resumen_cotizacion/{cotizacionId}") {
        fun crearRuta(cotizacionId: Long) = "resumen_cotizacion/$cotizacionId"
    }
    data object Cotizaciones : Screen("cotizaciones")
}
