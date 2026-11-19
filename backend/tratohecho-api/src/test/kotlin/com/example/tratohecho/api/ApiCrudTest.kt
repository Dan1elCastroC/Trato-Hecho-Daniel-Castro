package com.example.tratohecho.api

import com.example.tratohecho.api.service.CotizacionService
import com.example.tratohecho.api.model.LineaCotizacion
import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@SpringBootTest
@AutoConfigureMockMvc
class ApiCrudTest(
    @Autowired val mvc: MockMvc,
    @Autowired val mapper: ObjectMapper,
    @Autowired val cotizacionService: CotizacionService
) {
    private fun json(o: Any) = mapper.writeValueAsString(o)

    private fun crearCliente(nombre: String = "Ana"): Long {
        val body = mapOf("nombre" to nombre, "correo" to "ana@mail.cl", "telefono" to "123456")
        val res = mvc.perform(post("/api/clientes").contentType(MediaType.APPLICATION_JSON).content(json(body)))
            .andExpect(status().isCreated).andReturn()
        return mapper.readTree(res.response.contentAsString)["id"].asLong()
    }

    private fun cotizacionBody(clienteId: Long, estado: String = "Borrador") = mapOf(
        "clienteId" to clienteId, "descuentoPorcentaje" to 10.0, "impuestoPorcentaje" to 19.0, "estado" to estado,
        "lineas" to listOf(
            mapOf("concepto" to "Pintura", "cantidad" to 2, "precioUnitario" to 1000.0),
            mapOf("concepto" to "Mano de obra", "cantidad" to 1, "precioUnitario" to 500.0)
        )
    )

    @Test
    fun `crud de clientes`() {
        val id = crearCliente()
        mvc.perform(get("/api/clientes/$id")).andExpect(status().isOk).andExpect(jsonPath("$.nombre").value("Ana"))
        mvc.perform(get("/api/clientes")).andExpect(status().isOk)

        val nuevo = mapOf("nombre" to "Ana Maria", "correo" to "ana@mail.cl", "telefono" to "999")
        mvc.perform(put("/api/clientes/$id").contentType(MediaType.APPLICATION_JSON).content(json(nuevo)))
            .andExpect(status().isOk).andExpect(jsonPath("$.nombre").value("Ana Maria"))

        mvc.perform(delete("/api/clientes/$id")).andExpect(status().isNoContent)
        mvc.perform(get("/api/clientes/$id")).andExpect(status().isNotFound)
    }

    @Test
    fun `cliente invalido devuelve 400 con campos`() {
        val malo = mapOf("nombre" to "", "correo" to "no-es-correo", "telefono" to "1")
        mvc.perform(post("/api/clientes").contentType(MediaType.APPLICATION_JSON).content(json(malo)))
            .andExpect(status().isBadRequest)
            .andExpect(jsonPath("$.campos.nombre").exists())
            .andExpect(jsonPath("$.campos.correo").exists())
    }

    @Test
    fun `crud de cotizaciones con lineas y total`() {
        val cid = crearCliente("Beto")
        val res = mvc.perform(post("/api/cotizaciones").contentType(MediaType.APPLICATION_JSON).content(json(cotizacionBody(cid))))
            .andExpect(status().isCreated)
            .andExpect(jsonPath("$.lineas.length()").value(2))
            .andReturn()
        val body = mapper.readTree(res.response.contentAsString)
        val id = body["id"].asLong()
        // (2*1000 + 500) = 2500 ; -10% = 2250 ; +19% = 2677.5
        assertEquals(2677.5, body["total"].asDouble(), 0.001)

        mvc.perform(get("/api/cotizaciones/$id")).andExpect(status().isOk)
        mvc.perform(get("/api/cotizaciones?clienteId=$cid")).andExpect(jsonPath("$.length()").value(1))
        mvc.perform(get("/api/cotizaciones")).andExpect(status().isOk)

        mvc.perform(put("/api/cotizaciones/$id").contentType(MediaType.APPLICATION_JSON).content(json(cotizacionBody(cid, "Enviada"))))
            .andExpect(status().isOk).andExpect(jsonPath("$.estado").value("Enviada"))

        mvc.perform(patch("/api/cotizaciones/$id/estado").param("estado", "Aceptada"))
            .andExpect(status().isOk).andExpect(jsonPath("$.estado").value("Aceptada"))

        mvc.perform(delete("/api/cotizaciones/$id")).andExpect(status().isNoContent)
        mvc.perform(get("/api/cotizaciones/$id")).andExpect(status().isNotFound)
        mvc.perform(delete("/api/cotizaciones/$id")).andExpect(status().isNotFound)
    }

    @Test
    fun `cotizacion con cliente inexistente o sin lineas es rechazada`() {
        mvc.perform(post("/api/cotizaciones").contentType(MediaType.APPLICATION_JSON).content(json(cotizacionBody(99999))))
            .andExpect(status().isBadRequest)
        val cid = crearCliente("Carla")
        val sinLineas = cotizacionBody(cid) + ("lineas" to emptyList<Any>())
        mvc.perform(post("/api/cotizaciones").contentType(MediaType.APPLICATION_JSON).content(json(sinLineas)))
            .andExpect(status().isBadRequest)
        mvc.perform(put("/api/cotizaciones/99999").contentType(MediaType.APPLICATION_JSON).content(json(cotizacionBody(cid))))
            .andExpect(status().isNotFound)
    }

    @Test
    fun `eliminar cliente elimina sus cotizaciones`() {
        val cid = crearCliente("Dario")
        mvc.perform(post("/api/cotizaciones").contentType(MediaType.APPLICATION_JSON).content(json(cotizacionBody(cid))))
            .andExpect(status().isCreated)
        mvc.perform(delete("/api/clientes/$cid")).andExpect(status().isNoContent)
        mvc.perform(get("/api/cotizaciones?clienteId=$cid")).andExpect(jsonPath("$.length()").value(0))
        mvc.perform(delete("/api/clientes/$cid")).andExpect(status().isNotFound)
    }

    @Test
    fun `calculo de total`() {
        val lineas = listOf(LineaCotizacion("x", 3, 100.0))
        assertEquals(300.0, cotizacionService.calcularTotal(lineas, 0.0, 0.0), 0.001)
        assertEquals(238.0, cotizacionService.calcularTotal(lineas, 33.333333333, 19.0), 0.001)
        assertEquals(357.0, cotizacionService.calcularTotal(lineas, 0.0, 19.0), 0.001)
    }
}
