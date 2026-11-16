package com.example.tratohecho.data

import com.example.tratohecho.data.remote.Indicador
import com.example.tratohecho.data.remote.Indicadores
import com.example.tratohecho.data.remote.IndicadoresApi
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest

class TratoHechoRepositoryTest : StringSpec({

    "obtenerIndicadores devuelve lo que entrega la API simulada" {
        val respuesta = Indicadores(uf = Indicador(39_000.0), dolar = Indicador(950.5))
        val api = mockk<IndicadoresApi>()
        coEvery { api.obtenerIndicadores() } returns respuesta
        val repo = TratoHechoRepository(database = mockk<AppDatabase>(relaxed = true), indicadoresApi = api)

        runTest {
            val resultado = repo.obtenerIndicadores()
            resultado.dolar?.valor shouldBe 950.5
            resultado.uf?.valor shouldBe 39_000.0
        }
        coVerify(exactly = 1) { api.obtenerIndicadores() }
    }

    "si la API falla, el repositorio propaga la excepcion" {
        val api = mockk<IndicadoresApi>()
        coEvery { api.obtenerIndicadores() } throws java.io.IOException("sin red")
        val repo = TratoHechoRepository(database = mockk<AppDatabase>(relaxed = true), indicadoresApi = api)

        runTest {
            val fallo = runCatching { repo.obtenerIndicadores() }.exceptionOrNull()
            (fallo is java.io.IOException) shouldBe true
        }
    }
})
