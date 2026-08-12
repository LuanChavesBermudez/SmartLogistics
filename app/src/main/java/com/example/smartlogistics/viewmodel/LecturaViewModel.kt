package com.example.smartlogistics.viewmodel

import androidx.lifecycle.ViewModel
import com.example.smartlogistics.model.LecturaDao
import com.example.smartlogistics.model.LecturaEntity
import kotlinx.coroutines.flow.Flow

class LecturaViewModel(private val dao: LecturaDao) : ViewModel() {
    val history: Flow<List<LecturaEntity>> = dao.getAll()
}