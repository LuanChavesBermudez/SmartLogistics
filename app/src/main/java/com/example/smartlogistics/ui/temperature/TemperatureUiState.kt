package com.example.smartlogistics.ui.temperature

import com.example.smartlogistics.model.TemperatureReading

data class TemperatureUiState(
    val currentReading: TemperatureReading? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
)
