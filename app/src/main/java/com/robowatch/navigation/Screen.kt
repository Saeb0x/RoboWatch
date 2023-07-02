package com.robowatch.navigation

sealed class Screen(val route: String) {
    object Welcome : Screen(route = "welcome_screen")
    object Home : Screen(route = "home_screen")
    object Auth : Screen(route = "login_screen")
    object AuthS: Screen(route = "signup_screen")
}