package com.example.tratohecho.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
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

class AppViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = TratoHechoRepository(AppDatabase.getDatabase(application))

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

    private val _mensaje = MutableStateFlow("")
    val mensaje = _mensaje.asStateFlow()

    fun limpiarMensaje() {
        _mensaje.value = ""
    }

    fun guardarCliente(
        nombre: String,
        correo: String,
        telefono: String,
        onGuardado: (Long) -> Unit
    ) {
        viewModelScope.launch {
            val id = repository.crearCliente(
                Cliente(
                    nombre = nombre.trim(),
                    correo = correo.trim(),
                    telefono = telefono.trim()
                )
            )
            _mensaje.value = "Cliente guardado localmente"
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
        viewModelScope.launch {
            val cliente = repository.obtenerCliente(clienteId)
            if (cliente != null) {
                repository.actualizarCliente(
                    cliente.copy(
                        fotoUri = fotoUri,
                        latitud = latitud,
                        longitud = longitud
                    )
                )
                _mensaje.value = "Visita guardada con datos nativos"
            }
            onGuardado()
        }
    }

    fun guardarCotizacion(
        cotizacion: Cotizacion,
        onGuardado: (Long) -> Unit
    ) {
        viewModelScope.launch {
            val id = repository.crearCotizacion(cotizacion)
            _mensaje.value = "Cotización guardada en Room"
            onGuardado(id)
        }
    }

    fun cambiarEstadoCotizacion(id: Long, estado: String) {
        viewModelScope.launch {
            repository.cambiarEstadoCotizacion(id, estado)
            _mensaje.value = "Estado actualizado a $estado"
        }
    }
}
