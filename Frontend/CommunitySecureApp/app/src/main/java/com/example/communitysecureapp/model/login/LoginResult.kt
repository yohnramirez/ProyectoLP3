package com.example.communitysecureapp.model.login

data class LoginResult (
    val success: Boolean,
    val token: String? = null,
    val userName: String? = null,
    val expiry: Long? = null,
    val errorMessage: String? = null
)