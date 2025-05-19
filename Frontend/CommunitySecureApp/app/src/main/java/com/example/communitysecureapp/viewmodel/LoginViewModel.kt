package com.example.communitysecureapp.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.communitysecureapp.model.login.LoginRequest
import com.example.communitysecureapp.model.login.LoginResult
import com.example.communitysecureapp.repository.AuthRepository
import com.example.communitysecureapp.repository.UserSessionRepository
import com.example.communitysecureapp.utils.loader.LoaderStates
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val repository: AuthRepository,
    private val userSessionRepository: UserSessionRepository
) : ViewModel() {

    private val _state = MutableStateFlow<LoginResult?>(null)
    val state: StateFlow<LoginResult?> = _state.asStateFlow()

    private val _loaderState = MutableStateFlow(LoaderStates<LoginResult>())
    val loaderState: StateFlow<LoaderStates<LoginResult>> = _loaderState

    val jwtToken: StateFlow<String?> = userSessionRepository.jwtToken
    val userId: StateFlow<String?> = userSessionRepository.userId

    fun login(loginRequest: LoginRequest) {
        viewModelScope.launch {
            try {

                _loaderState.update { it.copy(isLoading = true) }

                val result = repository.login(loginRequest)

                if (result.success && result.token != null) {
                    userSessionRepository.saveSession(result.token)
                    Log.d(
                        "LoginViewModel",
                        "Login successful, token saved via UserSessionRepo. Current token in VM: ${jwtToken.value}"
                    )
                } else {
                    Log.w(
                        "LoginViewModel",
                        "Login failed or token was null. Success: ${result.success}"
                    )
                }

                _loaderState.update {
                    it.copy(
                        result = result,
                        isLoading = false,
                        errorMessage = null
                    )
                }

            } catch (e: Exception) {
                _loaderState.update {
                    it.copy(
                        result = null,
                        isLoading = false,
                        errorMessage = e.message
                    )
                }
            }
        }
    }

    fun logOut() {
        userSessionRepository.clearSession()
        _loaderState.update { LoaderStates() }
        _state.value = null
        Log.d("LoginViewModel", "User logged out.")
    }

    fun clearLoginViewModel() {
        _state.value = null
    }
}