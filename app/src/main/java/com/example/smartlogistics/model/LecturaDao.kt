package com.example.smartlogistics.model

import androidx.room3.Dao
import androidx.room3.Insert
import androidx.room3.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface LecturaDao {
    @Query("SELECT * FROM lecturas_temperatura ORDER BY fechaHora DESC")
    fun getAll(): Flow<List<LecturaEntity>>

    @Insert
    suspend fun insert(lectura: LecturaEntity)
}
