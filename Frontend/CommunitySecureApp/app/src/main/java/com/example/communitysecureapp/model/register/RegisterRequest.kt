package com.example.communitysecureapp.model.register

data class RegisterRequest (
    val email: String,
    var password: String,
    val address: String,
    val country: String,
    val city: String,
    val gender: String,
    val birthday: String,
    val numberDocument: String,
    val typeDocument: String
)