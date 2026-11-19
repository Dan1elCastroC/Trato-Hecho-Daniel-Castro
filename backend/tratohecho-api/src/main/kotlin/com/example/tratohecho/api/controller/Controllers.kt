package com.example.tratohecho.api.controller

import com.example.tratohecho.api.dto.*
import com.example.tratohecho.api.model.Cliente
import com.example.tratohecho.api.service.ClienteService
import com.example.tratohecho.api.service.CotizacionService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/clientes")
class ClienteController(private val service: ClienteService) {
    @GetMapping fun listar() = service.listar()
    @GetMapping("/{id}") fun obtener(@PathVariable id: Long) = service.obtener(id)

    @PostMapping @ResponseStatus(HttpStatus.CREATED)
    fun crear(@Valid @RequestBody r: ClienteRequest): Cliente = service.crear(r)

    @PutMapping("/{id}")
    fun actualizar(@PathVariable id: Long, @Valid @RequestBody r: ClienteRequest) = service.actualizar(id, r)

    @DeleteMapping("/{id}") @ResponseStatus(HttpStatus.NO_CONTENT)
    fun eliminar(@PathVariable id: Long) = service.eliminar(id)
}

@RestController
@RequestMapping("/api/cotizaciones")
class CotizacionController(private val service: CotizacionService) {
    @GetMapping fun listar(@RequestParam(required = false) clienteId: Long?) = service.listar(clienteId)
    @GetMapping("/{id}") fun obtener(@PathVariable id: Long) = service.obtener(id)

    @PostMapping @ResponseStatus(HttpStatus.CREATED)
    fun crear(@Valid @RequestBody r: CotizacionRequest) = service.crear(r)

    @PutMapping("/{id}")
    fun actualizar(@PathVariable id: Long, @Valid @RequestBody r: CotizacionRequest) = service.actualizar(id, r)

    @PatchMapping("/{id}/estado")
    fun cambiarEstado(@PathVariable id: Long, @RequestParam estado: String) = service.cambiarEstado(id, estado)

    @DeleteMapping("/{id}") @ResponseStatus(HttpStatus.NO_CONTENT)
    fun eliminar(@PathVariable id: Long) = service.eliminar(id)
}

@RestControllerAdvice
class ErrorHandler {
    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun validacion(e: MethodArgumentNotValidException): ResponseEntity<Map<String, Any>> {
        val errores = e.bindingResult.fieldErrors.associate { it.field to (it.defaultMessage ?: "invalido") }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(mapOf("error" to "Validacion fallida", "campos" to errores))
    }
}
