package com.example.monev.ui.navigation

sealed class Destinations(val route: String) {
    data object WelcomeScreen : Destinations("WelcomeScreen")
    data object HomeScreen: Destinations("HomeScreen")
    data object AccountScreen: Destinations("AccountScreen")
    data object SettingScreen: Destinations("SettingScreen")
    data object ChatbotScreen: Destinations("ChatbotScreen")

    // auth
    data object ProfileScreen: Destinations("ProfileScreen")
    data object SignInScreen: Destinations("SignInScreen")

    // Result Screen
    data object ResultScreen: Destinations("ResultScreen/{predictionResult}/{confidence}") {
        fun createRoute(predictionResult: String, confidence: Float) =
            "ResultScreen/${encode(predictionResult)}/$confidence"

        // Fungsi untuk meng-encode parameter jika diperlukan
        private fun encode(param: String): String {
            return java.net.URLEncoder.encode(param, "UTF-8")
        }
    }
}
