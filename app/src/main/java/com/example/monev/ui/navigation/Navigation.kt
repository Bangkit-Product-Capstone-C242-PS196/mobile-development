
package com.example.monev.ui.navigation

import androidx.compose.runtime.Composable
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

    NavHost(
        navController = navController,
        startDestination = Destinations.WelcomeScreen.route
    ) {
        composable(Destinations.WelcomeScreen.route) {
            WelcomeScreen(onNextClick = {
                navController.navigate(Destinations.HomeScreen.route)
            })
        }
        composable(Destinations.HomeScreen.route) {
            HomeScreen(navController = navController)
        }
        composable(Destinations.SettingScreen.route) {
            SettingScreen(navController = navController)
        }
        composable(Destinations.AccountScreen.route) {
            AccountScreen(navController = navController)
        }

    }


}