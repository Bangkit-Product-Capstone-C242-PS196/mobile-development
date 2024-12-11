package com.example.monev.ui.navigation

sealed class Destinations (val route: String) {
    data object WelcomeScreen : Destinations("WelcomeScreen")
    data object HomeScreen: Destinations("HomeScreen")
    data object AccountScreen: Destinations("AccountScreen")
    data object SettingScreen: Destinations("SettingScreen")
    data object ChatbotScreen: Destinations("ChatbotScreen")
    data object SplashScreen: Destinations("SplashScreen")

    // auth
    data object ProfileScreen: Destinations("ProfileScreen")
    data object SignInScreen: Destinations("SignInScreen")

    // about
    data object AboutScreen: Destinations("AboutScreen")



}