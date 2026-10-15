package com.example.tratohecho.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.tratohecho.ui.screens.ClienteDetalleScreen
import com.example.tratohecho.ui.screens.ClientesScreen
import com.example.tratohecho.ui.screens.CotizacionesScreen
import com.example.tratohecho.ui.screens.HomeScreen
import com.example.tratohecho.ui.screens.LoginScreen
import com.example.tratohecho.ui.screens.NuevaCotizacionScreen
import com.example.tratohecho.ui.screens.NuevoClienteScreen
import com.example.tratohecho.ui.screens.ResumenCotizacionScreen
import com.example.tratohecho.ui.screens.VisitaScreen
import com.example.tratohecho.viewmodel.AppViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppNavigation(viewModel: AppViewModel) {
    val navController = rememberNavController()
    val clientes by viewModel.clientes.collectAsStateWithLifecycle()
    val cotizaciones by viewModel.cotizaciones.collectAsStateWithLifecycle()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route
    val mostrarVolver = currentRoute != Screen.Login.route && currentRoute != Screen.Home.route

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Trato Hecho",
                        style = MaterialTheme.typography.titleLarge
                    )
                },
                navigationIcon = {
                    if (mostrarVolver) {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                        }
                    }
                }
            )
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Login.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Login.route) {
                LoginScreen(
                    onLoginCorrecto = {
                        navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.Login.route) { inclusive = true }
                        }
                    }
                )
            }

            composable(Screen.Home.route) {
                HomeScreen(
                    totalClientes = clientes.size,
                    totalCotizaciones = cotizaciones.size,
                    onClientes = { navController.navigate(Screen.Clientes.route) },
                    onCotizaciones = { navController.navigate(Screen.Cotizaciones.route) }
                )
            }

            composable(Screen.Clientes.route) {
                ClientesScreen(
                    clientes = clientes,
                    onNuevoCliente = { navController.navigate(Screen.NuevoCliente.route) },
                    onClienteSeleccionado = { clienteId ->
                        navController.navigate(Screen.DetalleCliente.crearRuta(clienteId))
                    },
                    onVerCotizaciones = { navController.navigate(Screen.Cotizaciones.route) }
                )
            }

            composable(Screen.NuevoCliente.route) {
                NuevoClienteScreen(
                    onGuardar = { nombre, correo, telefono ->
                        viewModel.guardarCliente(nombre, correo, telefono) { nuevoId ->
                            navController.navigate(Screen.DetalleCliente.crearRuta(nuevoId)) {
                                popUpTo(Screen.Clientes.route)
                            }
                        }
                    }
                )
            }

            composable(Screen.DetalleCliente.route) { entry ->
                val clienteId = entry.arguments?.getString("clienteId")?.toLongOrNull() ?: 0L
                val cliente = clientes.find { it.id == clienteId }
                val cotizacionesCliente = cotizaciones.filter { it.clienteId == clienteId }
                ClienteDetalleScreen(
                    cliente = cliente,
                    cotizaciones = cotizacionesCliente,
                    onNuevaCotizacion = { navController.navigate(Screen.NuevaCotizacion.crearRuta(clienteId)) },
                    onRegistrarVisita = { navController.navigate(Screen.Visita.crearRuta(clienteId)) },
                    onVerCotizaciones = { navController.navigate(Screen.Cotizaciones.route) },
                    onVolverClientes = { navController.navigate(Screen.Clientes.route) }
                )
            }

            composable(Screen.Visita.route) { entry ->
                val clienteId = entry.arguments?.getString("clienteId")?.toLongOrNull() ?: 0L
                val cliente = clientes.find { it.id == clienteId }
                VisitaScreen(
                    cliente = cliente,
                    onGuardarVisita = { fotoUri, latitud, longitud ->
                        viewModel.guardarVisita(clienteId, fotoUri, latitud, longitud) {
                            navController.navigate(Screen.DetalleCliente.crearRuta(clienteId)) {
                                popUpTo(Screen.Visita.route) { inclusive = true }
                            }
                        }
                    }
                )
            }

            composable(Screen.NuevaCotizacion.route) { entry ->
                val clienteId = entry.arguments?.getString("clienteId")?.toLongOrNull() ?: 0L
                val cliente = clientes.find { it.id == clienteId }
                NuevaCotizacionScreen(
                    cliente = cliente,
                    onGuardar = { cotizacion ->
                        viewModel.guardarCotizacion(cotizacion) { cotizacionId ->
                            navController.navigate(Screen.ResumenCotizacion.crearRuta(cotizacionId))
                        }
                    }
                )
            }

            composable(Screen.ResumenCotizacion.route) { entry ->
                val cotizacionId = entry.arguments?.getString("cotizacionId")?.toLongOrNull() ?: 0L
                val cotizacion = cotizaciones.find { it.id == cotizacionId }
                val cliente = clientes.find { it.id == cotizacion?.clienteId }
                ResumenCotizacionScreen(
                    cliente = cliente,
                    cotizacion = cotizacion,
                    onVerCotizaciones = { navController.navigate(Screen.Cotizaciones.route) },
                    onVolverClientes = {
                        if (cliente != null) {
                            navController.navigate(Screen.DetalleCliente.crearRuta(cliente.id))
                        } else {
                            navController.navigate(Screen.Clientes.route)
                        }
                    }
                )
            }

            composable(Screen.Cotizaciones.route) {
                CotizacionesScreen(
                    cotizaciones = cotizaciones,
                    clientes = clientes,
                    onCambiarEstado = viewModel::cambiarEstadoCotizacion,
                    onVolverClientes = { navController.navigate(Screen.Clientes.route) }
                )
            }
        }
    }
}
