package com.example.communitysecureapp.model.user

import com.example.communitysecureapp.model.login.User

data class UserResult (
    val success: Boolean,
    val data: User? = null,
    val errorMessage: String? = null
)