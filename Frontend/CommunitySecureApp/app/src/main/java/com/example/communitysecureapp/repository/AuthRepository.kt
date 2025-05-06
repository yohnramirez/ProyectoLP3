package com.example.communitysecureapp.repository

import android.util.Log
import com.example.communitysecureapp.model.login.LoginRequest
import com.example.communitysecureapp.model.login.LoginResult
import com.example.communitysecureapp.model.register.RegisterRequest
import com.example.communitysecureapp.model.register.RegisterResult
import com.example.communitysecureapp.service.ApiService
import javax.inject.Inject

class AuthRepository @Inject constructor (private val apiService: ApiService) {

    suspend fun login(loginRequest: LoginRequest): LoginResult {
        return try {
            val response = apiService.login(loginRequest)
            Log.e("LOGIN", response.toString())

            LoginResult(
                success = true,
                token = response.accessToken,
                userName = response.user.email,
                expiry = response.expiresAt,
                errorMessage = null
            )
        } catch (e: Exception) {
            LoginResult(
                success = false,
                token = null,
                userName = null,
                expiry = null,
                errorMessage = e.message ?: "Error al iniciar sesión"
            )
        }
    }

    suspend fun register(registerRequest: RegisterRequest): RegisterResult {
        return try {
            val response = apiService.register(registerRequest)
            Log.e("REGISTER", response.toString())

            RegisterResult(
                success = true,
                token = response.accessToken,
                userName = response.user.email,
                expiry = response.expiresAt,
                errorMessage = null
            )
        } catch (e: Exception) {
            RegisterResult(
                success = false,
                token = null,
                userName = null,
                expiry = null,
                errorMessage = e.message ?: "Error al registrar"
            )
        }
    }
}