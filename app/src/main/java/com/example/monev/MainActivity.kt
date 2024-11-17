package com.example.monev

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.monev.ui.screens.home.HomeScreen
import com.example.monev.ui.theme.MonevTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MonevTheme {
                HomeScreen()
            }
        }
    }
}
