package com.example.smartlogistics.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.smartlogistics.model.LecturaDao
import com.example.smartlogistics.model.LecturaEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class LecturaViewModel(private val dao: LecturaDao) : ViewModel() {
    val historial: Flow<List<LecturaEntity>> = dao.getAll()

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
}