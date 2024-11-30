
package com.example.monev.ui.navigation

import MyBottomBar
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.monev.ui.screens.account.AccountScreen
import com.example.monev.ui.screens.home.HomeScreen
import com.example.monev.ui.screens.setting.SettingScreen
import com.example.monev.ui.screens.welcome.WelcomeScreen




@Composable
fun Navigation(modifier: Modifier = Modifier) {
    val navController = rememberNavController()

    // Rute yang membutuhkan BottomBar
    val screensWithBottomBar = listOf(
        Destinations.HomeScreen.route,
        Destinations.SettingScreen.route
    )

    // State untuk menyimpan rute saat ini
    val currentRoute = remember { mutableStateOf<String?>(null) }

    // Update currentRoute setiap kali destinasi berubah
    LaunchedEffect(navController) {
        navController.addOnDestinationChangedListener { _, destination, _ ->
            currentRoute.value = destination.route
        }
    }

    Scaffold(
        bottomBar = {
            // Tampilkan BottomBar hanya di HomeScreen dan SettingScreen
            if (currentRoute.value in screensWithBottomBar) {
                MyBottomBar(navController = navController)
            }
        }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = Destinations.WelcomeScreen.route,
            modifier = Modifier.padding(paddingValues)
        ) {
            // WelcomeScreen (tanpa BottomBar)
            composable(Destinations.WelcomeScreen.route) {
                WelcomeScreen(onNextClick = {
                    navController.navigate(Destinations.HomeScreen.route) {
                        popUpTo(Destinations.WelcomeScreen.route) { inclusive = true }
                    }
                })
            }

            // HomeScreen (dengan BottomBar)
            composable(Destinations.HomeScreen.route) {
                HomeScreen(navController = navController)
            }

            // SettingScreen (dengan BottomBar)
            composable(Destinations.SettingScreen.route) {
                SettingScreen(navController = navController)
            }

            // AccountScreen (tanpa BottomBar)
            composable(Destinations.AccountScreen.route) {
                AccountScreen(navController = navController)
            }
            composable(Destinations.ChatbotScreen.route) {
                com.example.monev.ui.screens.chatbot.ChatbotScreen()
            }
        }
    }
}
