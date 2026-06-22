package com.trustlens.app.ui

sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Home : Screen("home")
    object Upload : Screen("upload")
    object Dashboard : Screen("dashboard")
}