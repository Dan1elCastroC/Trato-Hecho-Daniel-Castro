package com.example.tratohecho.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "clientes")
data class Cliente(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val nombre: String,
    val correo: String,
    val telefono: String,
    val empresa: String = "Empresa demo",
    val fotoUri: String = "",
    val latitud: Double? = null,
    val longitud: Double? = null
)

@Entity(
    tableName = "cotizaciones",
    foreignKeys = [
        ForeignKey(
            entity = Cliente::class,
            parentColumns = ["id"],
            childColumns = ["clienteId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("clienteId")]
)
data class Cotizacion(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val clienteId: Long,
    val concepto: String,
    val cantidad: Int,
    val precioUnitario: Double,
    val descuentoPorcentaje: Double,
    val impuestoPorcentaje: Double,
    val moneda: String,
    @ColumnInfo(defaultValue = "''") val lineasJson: String = "",
    val estado: String = "Borrador"
)
