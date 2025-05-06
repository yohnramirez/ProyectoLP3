package com.example.communitysecureapp.model.register

data class RegisterResult (
    val success: Boolean,
    val token: String? = null,
    val userName: String? = null,
    val expiry: Long? = null,
    val errorMessage: String? = null
)