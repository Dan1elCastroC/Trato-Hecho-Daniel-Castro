package com.example.tratohecho.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface CotizacionDao {
    @Query("SELECT * FROM cotizaciones ORDER BY id DESC")
    fun observarCotizaciones(): Flow<List<Cotizacion>>

    @Query("SELECT * FROM cotizaciones WHERE id = :id LIMIT 1")
    suspend fun obtenerCotizacion(id: Long): Cotizacion?

    @Insert
    suspend fun insertar(cotizacion: Cotizacion): Long

    @Query("UPDATE cotizaciones SET estado = :estado WHERE id = :id")
    suspend fun cambiarEstado(id: Long, estado: String)
}
