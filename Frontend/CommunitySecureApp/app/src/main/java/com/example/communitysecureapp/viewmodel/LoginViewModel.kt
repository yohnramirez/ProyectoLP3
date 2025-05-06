package com.example.communitysecureapp.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.communitysecureapp.model.login.LoginRequest
import com.example.communitysecureapp.model.login.LoginResult
import com.example.communitysecureapp.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(private val repository: AuthRepository) : ViewModel() {

    private val _state = MutableStateFlow<LoginResult?>(null)
    val state: StateFlow<LoginResult?> = _state

    fun login(loginRequest: LoginRequest) {
        viewModelScope.launch {
            try {
                Log.d("Login", "Intentando login con ${loginRequest.toString()}")
                val result = repository.login(loginRequest)
                Log.d("Login", "Resultado login: $result")
                _state.value = result
            } catch (e: Exception) {
                Log.d("Login", "Error login: ${e.message}")
                _state.value = null
            }
        }
    }
}