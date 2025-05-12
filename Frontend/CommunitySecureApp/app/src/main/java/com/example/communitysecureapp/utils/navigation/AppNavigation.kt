package com.example.communitysecureapp.utils.navigation

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.communitysecureapp.screen.HomeScreen
import com.example.communitysecureapp.screen.LoginScreen
import com.example.communitysecureapp.screen.MapSelectorScreen
import com.example.communitysecureapp.screen.RegisterScreen
import com.example.communitysecureapp.viewmodel.LoginViewModel
import com.example.communitysecureapp.viewmodel.RegisterViewModel
import org.osmdroid.util.GeoPoint

@Composable
fun AppNavigation(navController: NavHostController, isLogged: Boolean) {

    val startDestination = if (isLogged) Home else Login

    NavHost(navController = navController, startDestination = startDestination) {
        composable<Login> {
            val loginViewModel: LoginViewModel = hiltViewModel()
            LoginScreen(
                navController = navController,
                viewModel = loginViewModel
            )
        }

        composable<Register> {
            val registerViewModel: RegisterViewModel = hiltViewModel()
            RegisterScreen(
                navController = navController,
                viewModel = registerViewModel
            )
        }

        composable<Home> {
            HomeScreen(
                navController = navController
            )
        }

        composable(
            route = "map_selector?lat={lat}&lon={lon}",
            arguments = listOf(
                navArgument("lat") { type = NavType.StringType; nullable = true },
                navArgument("lon") { type = NavType.StringType; nullable = true }
            )
        ) { backStackEntry ->
            val latString = backStackEntry.arguments?.getString("lat")
            val lonString = backStackEntry.arguments?.getString("lon")
            val initialGeoPoint = if (latString != null && lonString != null) {
                try {
                    GeoPoint(latString.toDouble(), lonString.toDouble())
                } catch (e: NumberFormatException) {
                    null
                }
            } else {
                null
            }

            MapSelectorScreen(
                navController = navController,
                initialLocation = initialGeoPoint
            )
        }
    }
}