package com.example.tratohecho.data

import com.example.tratohecho.data.remote.ClienteDto
import com.example.tratohecho.data.remote.CotizacionDto
import com.example.tratohecho.data.remote.Indicador
import com.example.tratohecho.data.remote.Indicadores
import com.example.tratohecho.data.remote.IndicadoresApi
import com.example.tratohecho.data.remote.LineaDto
import com.example.tratohecho.data.remote.TratoHechoApi
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.coVerifyOrder
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import kotlinx.coroutines.test.runTest

/** Se mockean la API (Retrofit) y los DAO (Room): asi se prueba solo la logica del repositorio. */
class TratoHechoRepositoryTest : StringSpec({

    fun entorno(): Triple<TratoHechoApi, ClienteDao, CotizacionDao> {
        return Triple(mockk(), mockk(relaxed = true), mockk(relaxed = true))
    }

    fun repo(api: TratoHechoApi, cDao: ClienteDao, qDao: CotizacionDao, ind: IndicadoresApi = mockk()): TratoHechoRepository {
        val db = mockk<AppDatabase>()
        every { db.clienteDao() } returns cDao
        every { db.cotizacionDao() } returns qDao
        return TratoHechoRepository(db, ind, api)
    }

    val cliente = Cliente(nombre = "Ana", correo = "a@a.cl", telefono = "123")
    val cotizacion = Cotizacion(
        clienteId = 7, concepto = "Pintura", cantidad = 2, precioUnitario = 1000.0,
        descuentoPorcentaje = 0.0, impuestoPorcentaje = 19.0, moneda = "CLP"
    )

    "obtenerIndicadores devuelve lo que entrega la API externa simulada" {
        val ind = mockk<IndicadoresApi>()
        coEvery { ind.obtenerIndicadores() } returns Indicadores(uf = Indicador(39_000.0), dolar = Indicador(950.5))
        val (api, c, q) = entorno()
        runTest {
            val r = repo(api, c, q, ind).obtenerIndicadores()
            r.dolar?.valor shouldBe 950.5
            r.uf?.valor shouldBe 39_000.0
        }
    }

    "si la API externa falla, el repositorio propaga la excepcion" {
        val ind = mockk<IndicadoresApi>()
        coEvery { ind.obtenerIndicadores() } throws java.io.IOException("sin red")
        val (api, c, q) = entorno()
        runTest {
            val fallo = runCatching { repo(api, c, q, ind).obtenerIndicadores() }.exceptionOrNull()
            (fallo is java.io.IOException) shouldBe true
        }
    }

    "crearCliente envia el cliente al microservicio y guarda en Room con el id remoto" {
        val (api, c, q) = entorno()
        coEvery { api.crearCliente(any()) } returns ClienteDto(id = 42, nombre = "Ana", correo = "a@a.cl", telefono = "123")
        coEvery { c.insertar(any()) } returns 42
        val guardado = slot<Cliente>()
        runTest {
            val id = repo(api, c, q).crearCliente(cliente)
            id shouldBe 42L
        }
        coVerify { c.insertar(capture(guardado)) }
        guardado.captured.id shouldBe 42L
    }

    "crearCliente no toca Room si el microservicio falla" {
        val (api, c, q) = entorno()
        coEvery { api.crearCliente(any()) } throws java.io.IOException("servidor apagado")
        runTest {
            runCatching { repo(api, c, q).crearCliente(cliente) }.isFailure shouldBe true
        }
        coVerify(exactly = 0) { c.insertar(any()) }
    }

    "crearCliente falla si el microservicio no devuelve id" {
        val (api, c, q) = entorno()
        coEvery { api.crearCliente(any()) } returns ClienteDto(nombre = "Ana", correo = "a@a.cl", telefono = "1")
        runTest {
            runCatching { repo(api, c, q).crearCliente(cliente) }.isFailure shouldBe true
        }
    }

    "actualizarCliente hace PUT y luego actualiza Room" {
        val (api, c, q) = entorno()
        coEvery { api.actualizarCliente(any(), any()) } returns ClienteDto(id = 5, nombre = "Ana", correo = "a@a.cl", telefono = "1")
        runTest { repo(api, c, q).actualizarCliente(cliente.copy(id = 5)) }
        coVerifyOrder {
            api.actualizarCliente(5, any())
            c.actualizar(match { it.id == 5L })
        }
    }

    "eliminarCliente borra en el microservicio y en Room" {
        val (api, c, q) = entorno()
        coEvery { api.eliminarCliente(9) } returns Unit
        runTest { repo(api, c, q).eliminarCliente(9) }
        coVerifyOrder { api.eliminarCliente(9); c.eliminar(9) }
    }

    "obtenerCliente lee de Room" {
        val (api, c, q) = entorno()
        coEvery { c.obtenerCliente(3) } returns cliente.copy(id = 3)
        runTest { repo(api, c, q).obtenerCliente(3)?.id shouldBe 3L }
    }

    "crearCotizacion envia las lineas y guarda con el id remoto" {
        val (api, c, q) = entorno()
        val enviado = slot<CotizacionDto>()
        coEvery { api.crearCotizacion(capture(enviado)) } returns CotizacionDto(
            id = 11, clienteId = 7, moneda = "CLP", descuentoPorcentaje = 0.0,
            impuestoPorcentaje = 19.0, estado = "Borrador", lineas = listOf(LineaDto("Pintura", 2, 1000.0))
        )
        runTest { repo(api, c, q).crearCotizacion(cotizacion) shouldBe 11L }
        enviado.captured.lineas.size shouldBe 1
        enviado.captured.lineas[0].concepto shouldBe "Pintura"
        coVerify { q.insertar(match { it.id == 11L }) }
    }

    "cambiarEstadoCotizacion hace PATCH y actualiza Room" {
        val (api, c, q) = entorno()
        coEvery { api.cambiarEstado(4, "Aceptada") } returns mockk(relaxed = true)
        runTest { repo(api, c, q).cambiarEstadoCotizacion(4, "Aceptada") }
        coVerifyOrder { api.cambiarEstado(4, "Aceptada"); q.cambiarEstado(4, "Aceptada") }
    }

    "eliminarCotizacion borra remoto y local" {
        val (api, c, q) = entorno()
        coEvery { api.eliminarCotizacion(6) } returns Unit
        runTest { repo(api, c, q).eliminarCotizacion(6) }
        coVerifyOrder { api.eliminarCotizacion(6); q.eliminar(6) }
    }

    "sincronizar copia el microservicio a Room conservando la foto local y borrando lo que ya no existe" {
        val (api, c, q) = entorno()
        coEvery { api.listarClientes() } returns listOf(ClienteDto(id = 1, nombre = "Ana", correo = "a@a.cl", telefono = "1"))
        coEvery { api.listarCotizaciones() } returns listOf(
            CotizacionDto(
                id = 2, clienteId = 1, moneda = "CLP", descuentoPorcentaje = 0.0, impuestoPorcentaje = 19.0,
                estado = "Enviada", lineas = listOf(LineaDto("A", 1, 10.0), LineaDto("B", 2, 5.0))
            )
        )
        coEvery { c.obtenerCliente(1) } returns cliente.copy(id = 1, fotoUri = "content://foto")
        val clientesGuardados = slot<List<Cliente>>()
        val cotizacionesGuardadas = slot<List<Cotizacion>>()
        runTest { repo(api, c, q).sincronizar() }
        coVerify { c.upsertTodos(capture(clientesGuardados)) }
        coVerify { q.upsertTodas(capture(cotizacionesGuardadas)) }
        clientesGuardados.captured[0].fotoUri shouldBe "content://foto"
        cotizacionesGuardadas.captured[0].obtenerLineas().size shouldBe 2
        cotizacionesGuardadas.captured[0].concepto shouldBe "A"
        coVerify { c.eliminarFueraDe(listOf(1L)) }
        coVerify { q.eliminarFueraDe(listOf(2L)) }
    }

    "conversiones: cotizacion sin lineas remotas queda con valores por defecto" {
        val dto = CotizacionDto(
            id = 1, clienteId = 1, moneda = "CLP", descuentoPorcentaje = 0.0,
            impuestoPorcentaje = 0.0, estado = "Borrador", lineas = emptyList()
        )
        val c = dto.aCotizacion()
        c.cantidad shouldBe 1
        c.concepto shouldBe ""
        cliente.aDto().id shouldBe null
        cliente.copy(id = 4).aDto().id shouldBe 4L
    }
})
