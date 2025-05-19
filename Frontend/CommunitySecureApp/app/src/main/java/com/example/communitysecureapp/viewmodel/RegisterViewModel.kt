package com.example.communitysecureapp.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.communitysecureapp.model.document.TypeDocument
import com.example.communitysecureapp.model.document.TypeDocumentResult
import com.example.communitysecureapp.model.gender.GenderResult
import com.example.communitysecureapp.model.register.RegisterRequest
import com.example.communitysecureapp.model.register.RegisterResult
import com.example.communitysecureapp.repository.AuthRepository
import com.example.communitysecureapp.repository.ConfigurationRepository
import com.example.communitysecureapp.repository.UserSessionRepository
import com.example.communitysecureapp.state.RegisterState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val configurationRepository: ConfigurationRepository,
    private val userSessionRepository: UserSessionRepository
) : ViewModel() {

    private val _registerResult = MutableStateFlow<RegisterResult?>(null)
    val registerResult: StateFlow<RegisterResult?> = _registerResult

    private val _registerState = MutableStateFlow<RegisterState>(RegisterState.Idle)
    val registerState: StateFlow<RegisterState> = _registerState.asStateFlow()

    private val _typeDocuments = MutableStateFlow<TypeDocumentResult?>(null)
    val typeDocuments: StateFlow<TypeDocumentResult?> = _typeDocuments

    private val _genders = MutableStateFlow<GenderResult?>(null)
    var genders: StateFlow<GenderResult?> = _genders

    val jwtToken: StateFlow<String?> = userSessionRepository.jwtToken
    val userId: StateFlow<String?> = userSessionRepository.userId

    fun register(registerRequest: RegisterRequest) {
        viewModelScope.launch {
            try {
                Log.d("Register", "Intentando register con ${registerRequest.toString()}")
                val result = authRepository.register(registerRequest)

                if (result.success && result.token?.isNotEmpty() == true) {
                    userSessionRepository.saveSession(result.token)
                    _registerState.value = RegisterState.Success(
                        token = result.token,
                        userName = registerRequest.email
                    )
                } else {
                    _registerState.value = RegisterState.Error(result.errorMessage ?: "Error desconocido")
                }

            } catch (e: Exception) {
                Log.d("Register", "Error register: ${e.message}")
                _registerState.value = RegisterState.Error(e.message ?: "Error desconocido")
            }
        }
    }

    fun getTypesDocument() {
        viewModelScope.launch {
            try {
                Log.d("Obteniendo typeDocuments", "Intentando obtener docs")
                val result = configurationRepository.getTypesDocument()
                Log.d("Obteniendo typeDocuments", "Resultado ${result}")
                _typeDocuments.value = result
            } catch (e: Exception) {
                Log.d("Obteniendo typeDocuments", "Error ${e.message}")
                _typeDocuments.value = null
            }
        }
    }

    fun getGenders() {
        viewModelScope.launch {
            try {
                Log.d("Obteniendo genders", "Intentando obtener genders")
                val result = configurationRepository.getGenders()
                Log.d("Obteniendo genders", "Resultado ${result}")
                _genders.value = result
            } catch (e: Exception) {
                Log.d("Obteniendo genders", "Error ${e.message}")
                _genders.value = null
            }
        }
    }

    fun clearResults() {
        _registerResult.value = null
        _typeDocuments.value = null
        _genders.value = null
    }

    fun resetRegisterState() {
        _registerState.value = RegisterState.Idle
    }
}