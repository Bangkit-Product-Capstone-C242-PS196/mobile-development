package com.example.monev.ui.navigation

sealed class Destinations(val route: String) {

    data object WelcomeScreen : Destinations("WelcomeScreen")
    data object HomeScreen : Destinations("HomeScreen")
    data object AccountScreen : Destinations("AccountScreen")
    data object SettingScreen : Destinations("SettingScreen")
    data object ChatbotScreen : Destinations("ChatbotScreen")
    data object HistoryScreen : Destinations("HistoryScreen")

    // auth
    data object SignInScreen : Destinations("SignInScreen")

    // ResultScreen tanpa argumen
    data object ResultScreen : Destinations("ResultScreen")

    // ResultScreen dengan argumen
    data object ResultScreenArgs : Destinations("ResultScreen/{predictionResult}/{confidence}") {
        fun createRoute(predictionResult: String, confidence: Float): String {
            return "ResultScreen/${encode(predictionResult)}/$confidence"
        }

        private fun encode(param: String): String {
            return java.net.URLEncoder.encode(param, "UTF-8")
        }
    }
}
