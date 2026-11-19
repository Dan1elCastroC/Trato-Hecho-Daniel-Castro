package com.example.tratohecho.api.model

import jakarta.persistence.*

@Entity
@Table(name = "clientes")
class Cliente(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0,
    var nombre: String = "",
    var correo: String = "",
    var telefono: String = "",
    var empresa: String = "Empresa demo",
    var latitud: Double? = null,
    var longitud: Double? = null
)

@Entity
@Table(name = "cotizaciones")
class Cotizacion(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0,
    var clienteId: Long = 0,
    var moneda: String = "CLP",
    var descuentoPorcentaje: Double = 0.0,
    var impuestoPorcentaje: Double = 19.0,
    var estado: String = "Borrador",
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "lineas_cotizacion", joinColumns = [JoinColumn(name = "cotizacion_id")])
    var lineas: MutableList<LineaCotizacion> = mutableListOf()
)

@Embeddable
class LineaCotizacion(
    var concepto: String = "",
    var cantidad: Int = 1,
    var precioUnitario: Double = 0.0
)
