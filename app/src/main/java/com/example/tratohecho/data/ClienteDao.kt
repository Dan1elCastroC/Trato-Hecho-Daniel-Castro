package com.example.tratohecho.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface ClienteDao {
    @Query("SELECT * FROM clientes ORDER BY id DESC")
    fun observarClientes(): Flow<List<Cliente>>

    @Query("SELECT * FROM clientes WHERE id = :id LIMIT 1")
    suspend fun obtenerCliente(id: Long): Cliente?

    @Insert
    suspend fun insertar(cliente: Cliente): Long

    @Update
    suspend fun actualizar(cliente: Cliente)
}
