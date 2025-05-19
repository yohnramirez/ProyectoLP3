package com.example.communitysecureapp.state

sealed class CreateReportState {
    object Idle : CreateReportState()
    object Loading : CreateReportState()
    data class Success(val message: String) : CreateReportState()
    data class Error(val errorMessage: String) : CreateReportState()
}