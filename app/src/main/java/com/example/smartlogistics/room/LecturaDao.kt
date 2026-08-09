package com.example.smartlogistics.room

import androidx.room3.Dao
import androidx.room3.Delete
import androidx.room3.Insert
import androidx.room3.Query

@Dao
interface LecturaDao {
    @Query("SELECT * FROM lecturas_temperatura ORDER BY fechaHora DESC")
    suspend fun getAll(): List<LecturaEntity>

    @Insert
    suspend fun insert(lectura: LecturaEntity)

    @Delete
    suspend fun delete(lectura: LecturaEntity)
}