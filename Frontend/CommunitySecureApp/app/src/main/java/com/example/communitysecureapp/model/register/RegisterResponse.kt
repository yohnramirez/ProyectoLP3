package com.example.communitysecureapp.model.register

import com.example.communitysecureapp.model.login.User
import com.google.gson.annotations.SerializedName

data class RegisterResponse (
    @SerializedName("expires_at") val expiresAt: Long,
    @SerializedName("expires_in") val expiresIn: Long,
    @SerializedName("access_token") val accessToken: String,
    @SerializedName("refresh_token") val refreshToken: String,
    @SerializedName("token_type") val tokenType: String,
    val user: User
)