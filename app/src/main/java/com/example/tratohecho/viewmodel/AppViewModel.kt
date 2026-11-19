package com.example.tratohecho.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.tratohecho.data.AppDatabase
import com.example.tratohecho.data.Cliente
import com.example.tratohecho.data.Cotizacion
import com.example.tratohecho.data.TratoHechoRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

sealed interface IndicadoresUiState {
    data object Cargando : IndicadoresUiState
    data class Exito(val dolar: Double, val uf: Double) : IndicadoresUiState
    data class Error(val mensaje: String) : IndicadoresUiState
}

class AppViewModel(private val repository: TratoHechoRepository) : ViewModel() {

    /** Crea el ViewModel con su repositorio real (Room + Retrofit). */
    class Factory(private val application: Application) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T =
            AppViewModel(TratoHechoRepository(AppDatabase.getDatabase(application))) as T
    }

    val clientes = repository.clientes.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = emptyList()
    )

    val cotizaciones = repository.cotizaciones.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = emptyList()
    )

    private val _indicadores = MutableStateFlow<IndicadoresUiState>(IndicadoresUiState.Cargando)
    val indicadores = _indicadores.asStateFlow()

    init {
        sincronizar()
        cargarIndicadores()
    }

    fun cargarIndicadores() {
        viewModelScope.launch {
            _indicadores.value = IndicadoresUiState.Cargando
            _indicadores.value = try {
                val respuesta = repository.obtenerIndicadores()
                val dolar = respuesta.dolar?.valor
                val uf = respuesta.uf?.valor
                if (dolar != null && uf != null) {
                    IndicadoresUiState.Exito(dolar = dolar, uf = uf)
                } else {
                    IndicadoresUiState.Error("Respuesta incompleta del servicio")
                }
            } catch (e: Exception) {
                IndicadoresUiState.Error("Sin conexión: no se pudo obtener el dólar y la UF")
            }
        }
    }

    private val _mensaje = MutableStateFlow("")
    val mensaje = _mensaje.asStateFlow()

    fun limpiarMensaje() {
        _mensaje.value = ""
    }

    /** Ejecuta una operacion contra el microservicio y avisa si falla (sin tumbar la app). */
    private fun ejecutar(exito: String, bloque: suspend () -> Unit) {
        viewModelScope.launch {
            try {
                bloque()
                if (exito.isNotBlank()) _mensaje.value = exito
            } catch (e: Exception) {
                _mensaje.value = "No se pudo completar: revisa que el microservicio este encendido"
            }
        }
    }

    fun sincronizar() = ejecutar("") { repository.sincronizar() }

    fun guardarCliente(
        nombre: String,
        correo: String,
        telefono: String,
        onGuardado: (Long) -> Unit
    ) {
        ejecutar("Cliente guardado en el microservicio") {
            val id = repository.crearCliente(
                Cliente(nombre = nombre.trim(), correo = correo.trim(), telefono = telefono.trim())
            )
            onGuardado(id)
        }
    }

    fun guardarVisita(
        clienteId: Long,
        fotoUri: String,
        latitud: Double?,
        longitud: Double?,
        onGuardado: () -> Unit
    ) {
        ejecutar("Visita guardada con datos nativos") {
            val cliente = repository.obtenerCliente(clienteId)
            if (cliente != null) {
                repository.actualizarCliente(cliente.copy(fotoUri = fotoUri, latitud = latitud, longitud = longitud))
            }
            onGuardado()
        }
    }

    fun eliminarCliente(id: Long, onEliminado: () -> Unit) {
        ejecutar("Cliente eliminado") {
            repository.eliminarCliente(id)
            onEliminado()
        }
    }

    fun guardarCotizacion(
        cotizacion: Cotizacion,
        onGuardado: (Long) -> Unit
    ) {
        ejecutar("Cotizacion guardada en el microservicio") {
            onGuardado(repository.crearCotizacion(cotizacion))
        }
    }

    fun cambiarEstadoCotizacion(id: Long, estado: String) {
        ejecutar("Estado actualizado a $estado") { repository.cambiarEstadoCotizacion(id, estado) }
    }

    fun eliminarCotizacion(id: Long) {
        ejecutar("Cotizacion eliminada") { repository.eliminarCotizacion(id) }
    }
}
