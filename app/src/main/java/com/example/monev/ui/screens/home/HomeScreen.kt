package com.example.monev.ui.screens.home

import android.content.Intent
import android.provider.MediaStore
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import com.example.monev.sign_in.UserData

@Composable
fun HomeScreen(
    navController: NavController,
    userData: UserData?,
    onSignOut: ()-> Unit
) {
    LocalContext.current
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        // Handle the result if needed
    }
    val colorScheme = MaterialTheme.colorScheme

    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .background(colorScheme.background)
    ) {
        Text(text = "Home Screen",
            color= colorScheme.onBackground
        )
        Button(onClick = {
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            launcher.launch(intent)
        },
            colors = ButtonDefaults.buttonColors(
                containerColor = colorScheme.primary
            )
            ) {
            Text(text = "Open Camera")
        }
        Button(onClick = onSignOut) {
            Text(
                text = "Signout"
            )
        }
    }
}