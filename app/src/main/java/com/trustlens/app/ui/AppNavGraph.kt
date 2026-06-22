package com.trustlens.app.ui

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.trustlens.app.ui.screens.dashboard.DashboardScreen
import com.trustlens.app.ui.screens.home.HomeScreen
import com.trustlens.app.ui.screens.splash.SplashScreen
import com.trustlens.app.ui.screens.upload.UploadScreen
import com.trustlens.app.viewmodel.VerificationViewModel

@Composable
fun AppNavGraph(navController: NavHostController) {

    // Single shared ViewModel across Upload and Dashboard
    val verificationViewModel: VerificationViewModel = viewModel()

    NavHost(
        navController = navController,
        startDestination = Screen.Splash.route
    ) {
        composable(Screen.Splash.route) {
            SplashScreen(navController = navController)
        }
        composable(Screen.Home.route) {
            HomeScreen(navController = navController)
        }
        composable(Screen.Upload.route) {
            UploadScreen(
                navController = navController,
                viewModel = verificationViewModel
            )
        }
        composable(Screen.Dashboard.route) {
            DashboardScreen(
                navController = navController,
                viewModel = verificationViewModel
            )
        }
    }
}