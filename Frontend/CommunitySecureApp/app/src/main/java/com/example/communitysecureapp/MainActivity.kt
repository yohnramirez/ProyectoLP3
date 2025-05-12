package com.example.communitysecureapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.rememberNavController
import com.example.communitysecureapp.utils.navigation.AppNavigation
import dagger.hilt.android.AndroidEntryPoint
import androidx.core.content.edit
import org.osmdroid.config.Configuration
import java.io.File

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Auth configurations
        val prefs = getSharedPreferences("auth_prefs", MODE_PRIVATE)
        val token = prefs.getString("auth_token", null)
        val expiry = prefs.getLong("token_expiry", 0L)
        val currentTime = System.currentTimeMillis() / 1000
        val isLogged = token != null && expiry > currentTime

        if (token != null && expiry <= currentTime) {
            prefs.edit { clear() }
        }

        // OpenStreetMap configurations
        val osmConfig = Configuration.getInstance()
        osmConfig.load(applicationContext, getSharedPreferences("osmdroid", MODE_PRIVATE))
        osmConfig.userAgentValue = applicationContext.packageName

        val osmDataPath = File(applicationContext.filesDir, "osmdroid")
        osmConfig.osmdroidBasePath = osmDataPath

        val osmTileCachePath = File(osmDataPath, "tile")
        osmConfig.osmdroidTileCache = osmTileCachePath

        if (!osmDataPath.exists()) {
            osmDataPath.mkdirs()
        }

        if (!osmTileCachePath.exists()) {
            osmTileCachePath.mkdirs()
        }

        setContent {
            val navController = rememberNavController()
            AppNavigation(navController = navController, isLogged = isLogged)
        }
    }
}
