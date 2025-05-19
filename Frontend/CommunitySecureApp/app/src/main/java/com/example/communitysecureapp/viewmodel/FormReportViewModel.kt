package com.example.communitysecureapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.communitysecureapp.model.report.ReportRequest
import com.example.communitysecureapp.model.report.ReportResult
import com.example.communitysecureapp.model.type.ReportTypeResult
import com.example.communitysecureapp.repository.ConfigurationRepository
import com.example.communitysecureapp.state.CreateReportState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FormReportViewModel @Inject constructor(private val configurationRepository: ConfigurationRepository) :
    ViewModel() {

    private val _typeReports = MutableStateFlow<ReportTypeResult?>(null)
    val typeReports: StateFlow<ReportTypeResult?> = _typeReports

    private val _reportCreated = MutableStateFlow<CreateReportState>(CreateReportState.Idle)
    val reportCreated: StateFlow<CreateReportState> = _reportCreated.asStateFlow()

    fun createReport(reportRequest: ReportRequest) {
        viewModelScope.launch {
            try {
                val result = configurationRepository.createReport(reportRequest)

                if (result.success) {
                    _reportCreated.value = CreateReportState.Success("Reporte creado exitosamente!")
                } else {
                    _reportCreated.value = CreateReportState.Error("No se pudo crear el reporte. Intente de nuevo.")
                }
            } catch (e: Exception) {
                _reportCreated.value = CreateReportState.Error(e.message ?: "Ocurrió un error inesperado.")
            }
        }
    }

    fun getReportTypes() {
        viewModelScope.launch {
            try {
                val result = configurationRepository.getTypeReports()
                _typeReports.value = result
            } catch (e: Exception) {
                _typeReports.value = null
            }
        }
    }

    fun clearStateReportCreated() {
        _reportCreated.value = CreateReportState.Idle
    }
}