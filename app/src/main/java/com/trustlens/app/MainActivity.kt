package com.trustlens.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.rememberNavController
import com.trustlens.app.ui.AppNavGraph
import com.trustlens.app.ui.theme.TrustLensTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TrustLensTheme {
                val navController = rememberNavController()
                AppNavGraph(navController = navController)
            }
        }
    }
}