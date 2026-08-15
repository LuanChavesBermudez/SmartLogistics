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
    val registroMensaje: String? = null,
    val registroExitoso: Boolean = false,
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

    fun alternarSignoTemperatura() {
        val temperatura = _registroUiState.value.temperatura
        actualizarTemperatura(
            if (temperatura.startsWith("-")) temperatura.drop(1) else "-$temperatura"
        )
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
        val temperatura = validarTemperatura() ?: return

        _registroUiState.value = _registroUiState.value.copy(
            obteniendoUbicacion = true,
            coordenadas = null,
            ubicacionError = null,
            puedeSolicitarPermiso = false,
            mostrarAjustesAplicacion = false,
            mostrarAjustesUbicacion = false,
        )
        viewModelScope.launch {
            try {
                val coordenadas = locationProvider.obtenerUbicacionActual()
                if (coordenadas == null) {
                    _registroUiState.value = _registroUiState.value.copy(
                        obteniendoUbicacion = false,
                        ubicacionError = "No se pudo obtener la ubicación actual. Verifique que la ubicación esté activada.",
                        mostrarAjustesUbicacion = true,
                    )
                    return@launch
                }

                dao.insert(
                    LecturaEntity(
                        temperatura = temperatura,
                        latitud = coordenadas.latitud,
                        longitud = coordenadas.longitud,
                    )
                )
                _registroUiState.value = RegistroUiState(
                    coordenadas = coordenadas,
                    registroMensaje = "Lectura registrada correctamente.",
                    registroExitoso = true,
                )
            } catch (error: LocationDisabledException) {
                _registroUiState.value = _registroUiState.value.copy(
                    obteniendoUbicacion = false,
                    ubicacionError = "La ubicación del dispositivo está desactivada.",
                    mostrarAjustesUbicacion = true,
                )
            } catch (_: Exception) {
                _registroUiState.value = _registroUiState.value.copy(
                    obteniendoUbicacion = false,
                    registroMensaje = "No se pudo registrar la lectura. Inténtelo nuevamente.",
                    registroExitoso = false,
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
