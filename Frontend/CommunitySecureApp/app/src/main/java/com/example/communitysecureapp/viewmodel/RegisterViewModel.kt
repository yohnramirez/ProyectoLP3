package com.example.communitysecureapp.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.communitysecureapp.model.register.RegisterRequest
import com.example.communitysecureapp.model.register.RegisterResult
import com.example.communitysecureapp.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(private val repository: AuthRepository) : ViewModel() {

    private val _state = MutableStateFlow<RegisterResult?>(null)
    val state: StateFlow<RegisterResult?> = _state

    fun register(registerRequest: RegisterRequest) {
        viewModelScope.launch {
            try {
                Log.d("Register", "Intentando register con ${registerRequest.toString()}")
                val result = repository.register(registerRequest)
                Log.d("Register", "Resultado register: $result")
                _state.value = result
            } catch (e: Exception) {
                Log.d("Register", "Error register: ${e.message}")
                _state.value = null
            }
        }
    }
}