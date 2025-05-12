package com.example.communitysecureapp.model.gender

data class GenderResult (
    val success: Boolean,
    val data: List<Gender>?,
    val errorMessage: String?,
)