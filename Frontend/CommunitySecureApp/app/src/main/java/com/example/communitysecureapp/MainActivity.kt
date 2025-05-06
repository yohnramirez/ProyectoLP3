package com.example.communitysecureapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.communitysecureapp.model.Screen
import com.example.communitysecureapp.screen.LoginScreen
import com.example.communitysecureapp.screen.RegisterScreen
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val navController = rememberNavController()
            NavHost(navController = navController, startDestination = Screen.Login.route) {
                composable(Screen.Login.route) { LoginScreen(navController) }
                composable(Screen.Register.route) { RegisterScreen(navController) }
            }
        }
    }
}
