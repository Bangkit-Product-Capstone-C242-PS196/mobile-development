package com.example.monev.ui.navigation

sealed class Destinations (val route: String) {
    data object HomeScreen: Destinations("HomeScreen")
    data object AccountScreen: Destinations("AccountScreen")
    data object SettingScreen: Destinations("SettingScreen")
}