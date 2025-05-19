package com.example.communitysecureapp.repository

import android.content.Context
import android.util.Base64
import android.util.Log
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import javax.inject.Inject
import javax.inject.Singleton
import androidx.core.content.edit

@Singleton
class UserSessionRepository @Inject constructor(@ApplicationContext private val context: Context) {

    private val sharedPreferences = context.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)

    private val _jwtToken = MutableStateFlow<String?>(sharedPreferences.getString("jwt_token", null))
    val jwtToken: StateFlow<String?> = _jwtToken.asStateFlow()

    private val _userId = MutableStateFlow<String?>(null)
    val userId: StateFlow<String?> = _userId.asStateFlow()

    init {
        CoroutineScope(Dispatchers.Default).launch {
            _jwtToken.collectLatest { token ->
                val extractedId = extractUserIdFromJwtInternal(token)
                if (_userId.value != extractedId) {
                    _userId.value = extractedId
                }
                Log.d("UserSessionRepository", "Token collected, new User ID: ${_userId.value}")
            }
        }

        if (_userId.value == null && _jwtToken.value != null){
            _userId.value = extractUserIdFromJwtInternal(_jwtToken.value)
        }
        Log.d("UserSessionRepository", "Init - Token: ${_jwtToken.value}, UserID: ${_userId.value}")
    }

    fun saveSession(token: String) {
        sharedPreferences.edit {
            putString("jwt_token", token)
        }
        _jwtToken.value = token
        Log.d("UserSessionRepository", "Session saved. Token: $token")
    }

    fun clearSession() {
        sharedPreferences.edit {
            remove("jwt_token")
        }
        _jwtToken.value = null
        Log.d("UserSessionRepository", "Session cleared.")
    }

    private fun extractUserIdFromJwtInternal(token: String?): String? {
        if (token == null) return null
        return try {
            val parts = token.split(".")
            if (parts.size != 3) {
                Log.w("UserSessionRepository", "Invalid JWT format: Not 3 parts")
                return null
            }

            val decodedBytes = Base64.decode(parts[1], Base64.URL_SAFE or Base64.NO_WRAP)
            val payloadJson = String(decodedBytes, Charsets.UTF_8)
            val json = Json.parseToJsonElement(payloadJson).jsonObject

            val userIdValue = json["sub"]?.jsonPrimitive?.contentOrNull
                ?: json["user_id"]?.jsonPrimitive?.contentOrNull

            if (userIdValue == null) {
                Log.w("UserSessionRepository", "User ID (sub or user_id) not found in JWT payload.")
            }
            userIdValue
        } catch (e: Exception) {
            Log.e("UserSessionRepository", "Error extracting User ID from JWT: ${e.message}", e)
            null
        }
    }
}