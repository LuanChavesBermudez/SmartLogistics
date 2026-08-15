package com.example.smartlogistics.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.smartlogistics.location.Coordenadas
import com.example.smartlogistics.location.LocationProvider
import com.example.smartlogistics.location.LocationDisabledException
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
    val obteniendoUbicacion: Boolean = false,
    val coordenadas: Coordenadas? = null,
    val ubicacionError: String? = null,
    val puedeSolicitarPermiso: Boolean = false,
    val mostrarAjustesAplicacion: Boolean = false,
    val mostrarAjustesUbicacion: Boolean = false,
)

class LecturaViewModel(
    private val dao: LecturaDao,
    private val locationProvider: LocationProvider,
) : ViewModel() {
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

    fun obtenerUbicacion() {
        if (_registroUiState.value.obteniendoUbicacion) return

        _registroUiState.value = _registroUiState.value.copy(
            obteniendoUbicacion = true,
            coordenadas = null,
            ubicacionError = null,
            puedeSolicitarPermiso = false,
            mostrarAjustesAplicacion = false,
            mostrarAjustesUbicacion = false,
        )
        viewModelScope.launch {
            runCatching { locationProvider.obtenerUbicacionActual() }
                .onSuccess { coordenadas ->
                    _registroUiState.value = _registroUiState.value.copy(
                        obteniendoUbicacion = false,
                        coordenadas = coordenadas,
                        ubicacionError = if (coordenadas == null) {
                            "No se pudo obtener la ubicación actual. Verifique que la ubicación esté activada."
                        } else {
                            null
                        },
                        mostrarAjustesUbicacion = coordenadas == null,
                    )
                }
                .onFailure { error ->
                    _registroUiState.value = _registroUiState.value.copy(
                        obteniendoUbicacion = false,
                        ubicacionError = if (error is LocationDisabledException) {
                            "La ubicación del dispositivo está desactivada."
                        } else {
                            "Ocurrió un error al obtener la ubicación."
                        },
                        mostrarAjustesUbicacion = error is LocationDisabledException,
                    )
                }
        }
    }

    fun informarPermisoUbicacionDenegado(puedeSolicitarDeNuevo: Boolean) {
        _registroUiState.value = _registroUiState.value.copy(
            obteniendoUbicacion = false,
            coordenadas = null,
            ubicacionError = "Se necesita el permiso de ubicación para registrar la lectura.",
            puedeSolicitarPermiso = puedeSolicitarDeNuevo,
            mostrarAjustesAplicacion = !puedeSolicitarDeNuevo,
            mostrarAjustesUbicacion = false,
        )
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

    class Factory(
        private val dao: LecturaDao,
        private val locationProvider: LocationProvider,
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            require(modelClass.isAssignableFrom(LecturaViewModel::class.java)) {
                "ViewModel desconocido: ${modelClass.name}"
            }
            return LecturaViewModel(dao, locationProvider) as T
        }
    }
}
