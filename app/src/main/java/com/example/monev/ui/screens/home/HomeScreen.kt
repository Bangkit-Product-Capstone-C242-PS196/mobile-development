package com.example.monev.ui.screens.home

import androidx.compose.material3.Button

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.example.monev.ui.navigation.Destinations

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    navController: NavController

) {
    Column (
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
    ) {
        Text(
            text = "Home Screen"
        )
        Button(
            onClick = {
                navController.navigate(Destinations.SettingScreen.route)
            }
        ) {
            Text(
                text = "ggo setting screen"
            )
        }
        Button(
            onClick = {
                navController.navigate(Destinations.CameraScreen.route)
            }
        ) {
            Text(
                text = "Camera"
            )
        }
    }

}