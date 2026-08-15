package com.example.smartlogistics.viewmodel

import com.example.smartlogistics.model.LecturaDao
import com.example.smartlogistics.model.LecturaEntity
import com.example.smartlogistics.location.Coordenadas
import com.example.smartlogistics.location.LocationProvider
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class LecturaViewModelTest {
    private val viewmodel = LecturaViewModel(FakeLecturaDao(), FakeLocationProvider())

    @Test
    fun validarTemperatura_rechazaCampoVacio() {
        assertNull(viewmodel.validarTemperatura())
        assertEquals("Ingrese una temperatura.", viewmodel.registroUiState.value.temperaturaError)
        assertFalse(viewmodel.registroUiState.value.temperaturaValida)
    }

    @Test
    fun validarTemperatura_rechazaTextoNoNumerico() {
        viewmodel.actualizarTemperatura("frío")

        assertNull(viewmodel.validarTemperatura())
        assertEquals(
            "Ingrese un valor numérico válido.",
            viewmodel.registroUiState.value.temperaturaError,
        )
    }

    @Test
    fun validarTemperatura_aceptaComaDecimal() {
        viewmodel.actualizarTemperatura("-2,5")

        assertEquals(-2.5, viewmodel.validarTemperatura()!!, 0.0)
        assertNull(viewmodel.registroUiState.value.temperaturaError)
        assertTrue(viewmodel.registroUiState.value.temperaturaValida)
    }

    @Test
    fun editarTemperatura_limpiaLaValidacionAnterior() {
        viewmodel.actualizarTemperatura("4")
        viewmodel.validarTemperatura()

        viewmodel.actualizarTemperatura("5")

        assertFalse(viewmodel.registroUiState.value.temperaturaValida)
        assertNull(viewmodel.registroUiState.value.temperaturaError)
    }

    @Test
    fun informarPermisoDenegado_muestraErrorDeUbicacion() {
        viewmodel.informarPermisoUbicacionDenegado(puedeSolicitarDeNuevo = true)

        assertEquals(
            "Se necesita el permiso de ubicación para registrar la lectura.",
            viewmodel.registroUiState.value.ubicacionError,
        )
        assertNull(viewmodel.registroUiState.value.coordenadas)
        assertFalse(viewmodel.registroUiState.value.obteniendoUbicacion)
        assertTrue(viewmodel.registroUiState.value.puedeSolicitarPermiso)
        assertFalse(viewmodel.registroUiState.value.mostrarAjustesAplicacion)
    }
}

private class FakeLocationProvider : LocationProvider {
    override suspend fun obtenerUbicacionActual() = Coordenadas(9.93, -84.08)
}

private class FakeLecturaDao : LecturaDao {
    private val lecturas = MutableStateFlow<List<LecturaEntity>>(emptyList())

    override fun getAll(): Flow<List<LecturaEntity>> = lecturas

    override suspend fun insert(lectura: LecturaEntity) = Unit

    override fun getById(id: Long): Flow<LecturaEntity?> = MutableStateFlow(null)

    override suspend fun delete(lectura: LecturaEntity) = Unit
}
