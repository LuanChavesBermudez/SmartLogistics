package com.example.smartlogistics.model

data class TemperatureReading(
    val temperature: Double,
    val unit: String,
    val timestamp: Long,
)
