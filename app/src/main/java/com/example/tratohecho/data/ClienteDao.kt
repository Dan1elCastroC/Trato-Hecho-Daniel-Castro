package com.example.tratohecho.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface ClienteDao {
    @Query("SELECT * FROM clientes ORDER BY id DESC")
    fun observarClientes(): Flow<List<Cliente>>

    @Query("SELECT * FROM clientes WHERE id = :id LIMIT 1")
    suspend fun obtenerCliente(id: Long): Cliente?

    @Insert
    suspend fun insertar(cliente: Cliente): Long

    @Upsert
    suspend fun upsertTodos(clientes: List<Cliente>)

    @Query("DELETE FROM clientes WHERE id NOT IN (:ids)")
    suspend fun eliminarFueraDe(ids: List<Long>)

    @Query("DELETE FROM clientes WHERE id = :id")
    suspend fun eliminar(id: Long)

    @Update
    suspend fun actualizar(cliente: Cliente)
}
