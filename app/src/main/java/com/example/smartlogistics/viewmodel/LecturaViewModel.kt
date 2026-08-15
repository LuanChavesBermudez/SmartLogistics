package com.example.smartlogistics.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.smartlogistics.model.LecturaDao
import com.example.smartlogistics.model.LecturaEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class RegistroUiState(
    val temperatura: String = "",
    val temperaturaError: String? = null,
    val temperaturaValida: Boolean = false,
)

class LecturaViewModel(private val dao: LecturaDao) : ViewModel() {
    val historial: Flow<List<LecturaEntity>> = dao.getAll()

    private val _registroUiState = MutableStateFlow(RegistroUiState())
    val registroUiState: StateFlow<RegistroUiState> = _registroUiState.asStateFlow()

    fun actualizarTemperatura(valor: String) {
        _registroUiState.value = RegistroUiState(temperatura = valor)
    }

    fun validarTemperatura(): Double? {
        val texto = _registroUiState.value.temperatura.trim()
        val temperatura = texto.replace(',', '.').toDoubleOrNull()
        val error = when {
            texto.isEmpty() -> "Ingrese una temperatura."
            temperatura == null || !temperatura.isFinite() -> "Ingrese un valor numérico válido."
            else -> null
        }

        _registroUiState.value = _registroUiState.value.copy(
            temperaturaError = error,
            temperaturaValida = error == null,
        )
        return if (error == null) temperatura else null
    }

    fun insertarLectura(lectura: LecturaEntity) {
        viewModelScope.launch {
            dao.insert(lectura)
        }
    }

    fun buscarLectura(id: Long): Flow<LecturaEntity?> {
        return dao.getById(id)
    }

    fun borrarLectura(lectura: LecturaEntity) {
        viewModelScope.launch {
            dao.delete(lectura)
        }
    }

    class Factory(private val dao: LecturaDao) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            require(modelClass.isAssignableFrom(LecturaViewModel::class.java)) {
                "ViewModel desconocido: ${modelClass.name}"
            }
            return LecturaViewModel(dao) as T
        }
    }
}
