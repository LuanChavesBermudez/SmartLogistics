package com.example.smartlogistics.room

import androidx.room3.Entity
import androidx.room3.PrimaryKey

@Entity(tableName = "lecturas_temperatura")
data class LecturaEntity (
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val temperatura: Double,
    val latitud: Double,
    val longitud: Double,
    val fechaHora: Long = System.currentTimeMillis()
)