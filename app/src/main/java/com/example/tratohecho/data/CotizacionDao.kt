package com.example.tratohecho.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface CotizacionDao {
    @Query("SELECT * FROM cotizaciones ORDER BY id DESC")
    fun observarCotizaciones(): Flow<List<Cotizacion>>

    @Query("SELECT * FROM cotizaciones WHERE id = :id LIMIT 1")
    suspend fun obtenerCotizacion(id: Long): Cotizacion?

    @Insert
    suspend fun insertar(cotizacion: Cotizacion): Long

    @Upsert
    suspend fun upsertTodas(cotizaciones: List<Cotizacion>)

    @Query("DELETE FROM cotizaciones WHERE id NOT IN (:ids)")
    suspend fun eliminarFueraDe(ids: List<Long>)

    @Query("DELETE FROM cotizaciones WHERE id = :id")
    suspend fun eliminar(id: Long)

    @Query("UPDATE cotizaciones SET estado = :estado WHERE id = :id")
    suspend fun cambiarEstado(id: Long, estado: String)
}
