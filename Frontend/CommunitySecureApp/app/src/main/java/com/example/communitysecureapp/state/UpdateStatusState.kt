package com.example.communitysecureapp.state

sealed class UpdateStatusState {
    object Idle : UpdateStatusState()
    object Loading : UpdateStatusState()
    data class Success(val message: String) : UpdateStatusState()
    data class Error(val errorMessage: String) : UpdateStatusState()
}