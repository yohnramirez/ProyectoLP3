package com.example.communitysecureapp.model

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Register : Screen("register")
}