package com.example.communitysecureapp.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.communitysecureapp.model.type.ReportTypeResult
import com.example.communitysecureapp.repository.ConfigurationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FormReportViewModel @Inject constructor(private val configurationRepository: ConfigurationRepository) :
    ViewModel() {

    private val _typeReports = MutableStateFlow<ReportTypeResult?>(null)
    val typeReports: StateFlow<ReportTypeResult?> = _typeReports

    fun getReportTypes() {
        viewModelScope.launch {
            try {
                Log.d("Obteniendo typeReports", "Intentando obtener types")
                val result = configurationRepository.getTypeReports()
                Log.d("Obteniendo typeReports", "Resultado $result")
                _typeReports.value = result
            } catch (e: Exception) {
                Log.d("Obteniendo typeReports", "Error ${e.message}")
                _typeReports.value = null
            }
        }
    }
}