package com.example.communitysecureapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.communitysecureapp.model.login.LoginRequest
import com.example.communitysecureapp.model.login.LoginResult
import com.example.communitysecureapp.repository.AuthRepository
import com.example.communitysecureapp.utils.loader.LoaderStates
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(private val repository: AuthRepository) : ViewModel() {

    private val _state = MutableStateFlow<LoginResult?>(null)
    val state: StateFlow<LoginResult?> = _state.asStateFlow()

    private val _loaderState = MutableStateFlow(LoaderStates<LoginResult>())
    val loaderState: StateFlow<LoaderStates<LoginResult>> = _loaderState

    fun login(loginRequest: LoginRequest) {
        viewModelScope.launch {
            try {

                _loaderState.update { it.copy(isLoading = true) }

                val result = repository.login(loginRequest)

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

    fun clearLoginViewModel() {
        _state.value = null
    }
}