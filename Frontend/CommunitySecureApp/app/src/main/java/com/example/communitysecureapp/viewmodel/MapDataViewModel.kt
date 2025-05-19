package com.example.communitysecureapp.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.communitysecureapp.model.comment.CommentListResult
import com.example.communitysecureapp.model.comment.CommentRequest
import com.example.communitysecureapp.model.report.MapMarkerData
import com.example.communitysecureapp.model.report.ReportListResult
import com.example.communitysecureapp.model.report.ReportResponse
import com.example.communitysecureapp.repository.ConfigurationRepository
import com.example.communitysecureapp.state.CreateCommentState
import com.example.communitysecureapp.state.MyReportsState
import com.example.communitysecureapp.state.UpdateStatusState
import com.example.communitysecureapp.utils.socket.ReportSocketManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.osmdroid.util.GeoPoint
import javax.inject.Inject

@HiltViewModel
class MapDataViewModel @Inject constructor(
    private val reportSocketManager: ReportSocketManager,
    private val configurationRepository: ConfigurationRepository
) :
    ViewModel() {

    private val _reportMarkers = MutableStateFlow<List<MapMarkerData>>(emptyList())
    val reportMarkers: StateFlow<List<MapMarkerData>> = _reportMarkers.asStateFlow()

    private val _comments = MutableStateFlow<CommentListResult?>(null)
    val comments: StateFlow<CommentListResult?> = _comments

    private val _commentCreated = MutableStateFlow<CreateCommentState>(CreateCommentState.Idle)
    val commentCreated: StateFlow<CreateCommentState> = _commentCreated.asStateFlow()

    private val _updateStatusState = MutableStateFlow<UpdateStatusState>(UpdateStatusState.Idle)
    val updateStatusState: StateFlow<UpdateStatusState> = _updateStatusState.asStateFlow()

    private val _usernames = MutableStateFlow<Map<String, String>>(emptyMap())
    val usernames: StateFlow<Map<String, String>> = _usernames

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _myReports = MutableStateFlow<ReportListResult?>(null)
    val myReportsState: StateFlow<MyReportsState?> = _myReports.map { result ->
        when {
            result == null -> MyReportsState.Loading
            !result.success -> MyReportsState.Error(result.errorMessage ?: "Error desconocido")
            result.data.isNullOrEmpty() -> MyReportsState.Empty
            else -> MyReportsState.Success(result.data)
        }
    }.stateIn(viewModelScope, SharingStarted.Lazily, MyReportsState.Loading)

    init {
        connectToReportsTopic()
        observeNewReports()
        getPendingReports()
    }

    private fun connectToReportsTopic() {
        Log.d("MapDataViewModel", "Intentando conectar a WebSocket...")
        reportSocketManager.connect()
    }

    private fun observeNewReports() {
        viewModelScope.launch {
            reportSocketManager.newReportFlow
                .collect { reportDto ->
                    Log.i(
                        "MapDataViewModel",
                        "Nuevo reporte recibido desde WebSocket: ${reportDto.id}"
                    )
                    val newMarker = MapMarkerData(
                        id = reportDto.id,
                        userId = reportDto.userId,
                        geoPoint = GeoPoint(reportDto.latitude, reportDto.longitude),
                        title = "Reporte: ${reportDto.type}",
                        snippet = reportDto.description.take(100),
                        typeName = reportDto.type,
                        status = reportDto.status,
                        dateCreated = reportDto.dateCreated
                    )

                    _reportMarkers.update { currentMarkers ->
                        if (currentMarkers.any { it.id == newMarker.id }) {
                            currentMarkers
                        } else {
                            currentMarkers + newMarker
                        }
                    }
                }
        }
    }

    fun getPendingReports() {
        viewModelScope.launch {
            try {
                val result = configurationRepository.getReportsByStatus("PENDIENTE")
                Log.d("PENDING REPORTS", result.toString())
                _reportMarkers.value = result.data?.map { it.toReportData() } ?: emptyList()
            } catch (e: Exception) {
                Log.e("Error pendingReports", e.message.toString())
                _reportMarkers.value = emptyList()
            }
        }
    }

    fun getReportsByUserId(userId: String) {
        viewModelScope.launch {
            try {
                val result = configurationRepository.getReportsByUserId(userId)

                if (result.success) {
                    _myReports.value = result
                } else {
                    _myReports.value = null
                }
            } catch (e: Exception) {
                _myReports.value = null
            }
        }
    }

    fun getUserById(userId: String) {
        viewModelScope.launch {
            try {
                val result = configurationRepository.getUserById(userId)

                if (result.success) {
                    val username = result.data?.email?.substringBefore("@") ?: "Desconocido"
                    _usernames.update { it + (userId to username) }
                } else {
                    _usernames.update { it + (userId to "Desconocido") }
                }
            } catch (e: Exception) {
                _usernames.update { it + (userId to "Desconocido") }
            }
        }
    }

    fun fetchUsernameIfNeeded(userId: String?) {
        if (userId == null || _usernames.value.containsKey(userId)) return
        getUserById(userId)
    }

    fun updateReportStatus(idReport: Long, status: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _updateStatusState.value = UpdateStatusState.Loading
            try {
                val result = configurationRepository.updateReportStatus(idReport, status)
                Log.d("ACTUALIZANDO STATUS", "$result")

                if (result.success) {
                    _reportMarkers.update { currentList ->
                        currentList.map {
                            if (it.id == idReport) it.copy(status = status) else it
                        }
                    }
                    _updateStatusState.value = UpdateStatusState.Success("Estado actualizado a $status")
                } else {
                    _updateStatusState.value = UpdateStatusState.Error("Error al actualizar: ${result.errorMessage ?: "Desconocido"}")
                }

            } catch (e: Exception) {
                Log.e("ERROR", "Error actualizando estado: ${e.message}")
                _updateStatusState.value = UpdateStatusState.Error(e.message ?: "Error desconocido")
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun getCommentsForReport(reportId: Long) {
        viewModelScope.launch {
            try {
                val result = configurationRepository.getCommentsByReportId(reportId)

                if (result.success && result.data != null) {
                    _comments.value = result
                }

            } catch (e: Exception) {
                Log.e("Error getCommentsForReport", e.message.toString())
                _comments.value = null
            }
        }
    }

    fun addComment(comment: CommentRequest) {
        viewModelScope.launch {
            try {
                val result = configurationRepository.createComment(comment)

                if (result.success) {
                    _commentCreated.value =
                        CreateCommentState.Success("Comentario creado exitosamente!")
                    getCommentsForReport(comment.reportId)
                } else {
                    _commentCreated.value =
                        CreateCommentState.Error("No se pudo crear el comentario")
                }
            } catch (e: Exception) {
                Log.e("Error addComment", e.message.toString())
                _commentCreated.value =
                    CreateCommentState.Error(e.message ?: "Ocurrió un error inesperado.")
            }
        }

    }

    fun ReportResponse.toReportData(): MapMarkerData {
        return MapMarkerData(
            id = this.id,
            userId = userId.toString(),
            geoPoint = GeoPoint(this.latitude, this.longitude),
            title = "Reporte: $type",
            snippet = description.take(100),
            typeName = type.name,
            status = status,
            dateCreated = dateCreated
        )
    }

    fun resetCommentCreatedState() {
        _commentCreated.value = CreateCommentState.Idle
    }

    fun resetUpdateStatusState() {
        _updateStatusState.value = UpdateStatusState.Idle
    }

    override fun onCleared() {
        super.onCleared()
        Log.d("MapDataViewModel", "Desconectando WebSocket en onCleared.")
        reportSocketManager.disconnect()
    }
}