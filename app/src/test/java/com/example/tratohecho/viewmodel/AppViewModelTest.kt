package com.example.tratohecho.viewmodel

import com.example.tratohecho.data.Cliente
import com.example.tratohecho.data.Cotizacion
import com.example.tratohecho.data.TratoHechoRepository
import com.example.tratohecho.data.remote.Indicador
import com.example.tratohecho.data.remote.Indicadores
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain

/** El repositorio se mockea; el ViewModel corre con un dispatcher de prueba (sin Android). */
class AppViewModelTest : StringSpec({

    beforeSpec { Dispatchers.setMain(UnconfinedTestDispatcher()) }
    afterSpec { Dispatchers.resetMain() }

    fun repoSimulado(indicadores: Indicadores? = Indicadores(Indicador(39_000.0), Indicador(950.0))): TratoHechoRepository {
        val repo = mockk<TratoHechoRepository>(relaxed = true)
        every { repo.clientes } returns flowOf(emptyList())
        every { repo.cotizaciones } returns flowOf(emptyList())
        coEvery { repo.sincronizar() } returns Unit
        if (indicadores != null) coEvery { repo.obtenerIndicadores() } returns indicadores
        else coEvery { repo.obtenerIndicadores() } throws java.io.IOException("sin red")
        return repo
    }

    "al iniciar sincroniza con el microservicio y carga indicadores" {
        val repo = repoSimulado()
        val vm = AppViewModel(repo)
        coVerify(exactly = 1) { repo.sincronizar() }
        val estado = vm.indicadores.value
        estado.shouldBeInstanceOf<IndicadoresUiState.Exito>()
        estado.dolar shouldBe 950.0
        estado.uf shouldBe 39_000.0
    }

    "indicadores incompletos producen estado de error" {
        val vm = AppViewModel(repoSimulado(Indicadores(uf = null, dolar = Indicador(1.0))))
        vm.indicadores.value.shouldBeInstanceOf<IndicadoresUiState.Error>()
    }

    "si la API externa falla se muestra el error y reintentar funciona" {
        val repo = repoSimulado(null)
        val vm = AppViewModel(repo)
        vm.indicadores.value.shouldBeInstanceOf<IndicadoresUiState.Error>()
        coEvery { repo.obtenerIndicadores() } returns Indicadores(Indicador(1.0), Indicador(2.0))
        vm.cargarIndicadores()
        vm.indicadores.value.shouldBeInstanceOf<IndicadoresUiState.Exito>()
    }

    "guardarCliente recorta los textos, avisa y devuelve el id" {
        val repo = repoSimulado()
        coEvery { repo.crearCliente(any()) } returns 15L
        val vm = AppViewModel(repo)
        var idRecibido = 0L
        vm.guardarCliente("  Ana  ", " a@a.cl ", " 123 ") { idRecibido = it }
        idRecibido shouldBe 15L
        coVerify { repo.crearCliente(match { it.nombre == "Ana" && it.correo == "a@a.cl" && it.telefono == "123" }) }
        vm.mensaje.value shouldBe "Cliente guardado en el microservicio"
        vm.limpiarMensaje()
        vm.mensaje.value shouldBe ""
    }

    "si el microservicio esta apagado se informa y no se navega" {
        val repo = repoSimulado()
        coEvery { repo.crearCliente(any()) } throws java.io.IOException("apagado")
        val vm = AppViewModel(repo)
        var navego = false
        vm.guardarCliente("Ana", "a@a.cl", "1") { navego = true }
        navego shouldBe false
        vm.mensaje.value.contains("microservicio") shouldBe true
    }

    "guardarVisita actualiza el cliente con foto y GPS" {
        val repo = repoSimulado()
        coEvery { repo.obtenerCliente(1) } returns Cliente(id = 1, nombre = "Ana", correo = "a", telefono = "1")
        val vm = AppViewModel(repo)
        var listo = false
        vm.guardarVisita(1, "content://foto", -33.4, -70.6) { listo = true }
        listo shouldBe true
        coVerify { repo.actualizarCliente(match { it.fotoUri == "content://foto" && it.latitud == -33.4 }) }
    }

    "guardarVisita de un cliente inexistente no actualiza nada" {
        val repo = repoSimulado()
        coEvery { repo.obtenerCliente(99) } returns null
        val vm = AppViewModel(repo)
        vm.guardarVisita(99, "", null, null) {}
        coVerify(exactly = 0) { repo.actualizarCliente(any()) }
    }

    "eliminarCliente y eliminarCotizacion llaman al repositorio" {
        val repo = repoSimulado()
        val vm = AppViewModel(repo)
        var eliminado = false
        vm.eliminarCliente(3) { eliminado = true }
        vm.eliminarCotizacion(4)
        eliminado shouldBe true
        coVerify { repo.eliminarCliente(3) }
        coVerify { repo.eliminarCotizacion(4) }
        vm.mensaje.value shouldBe "Cotizacion eliminada"
    }

    "guardarCotizacion y cambiarEstado delegan en el repositorio" {
        val repo = repoSimulado()
        val cot = Cotizacion(
            clienteId = 1, concepto = "x", cantidad = 1, precioUnitario = 1.0,
            descuentoPorcentaje = 0.0, impuestoPorcentaje = 0.0, moneda = "CLP"
        )
        coEvery { repo.crearCotizacion(cot) } returns 8L
        val vm = AppViewModel(repo)
        var id = 0L
        vm.guardarCotizacion(cot) { id = it }
        id shouldBe 8L
        vm.cambiarEstadoCotizacion(8, "Aceptada")
        coVerify { repo.cambiarEstadoCotizacion(8, "Aceptada") }
        vm.mensaje.value shouldBe "Estado actualizado a Aceptada"
    }
})
