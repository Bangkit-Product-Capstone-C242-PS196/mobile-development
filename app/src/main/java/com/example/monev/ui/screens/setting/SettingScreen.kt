package com.example.monev.ui.screens.setting

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.monev.ui.navigation.Destinations

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingScreen(
    modifier: Modifier = Modifier,
    navController: NavController,
    onSignOut: ()-> Unit
) {
    val colorScheme = MaterialTheme.colorScheme

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Setting",
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                            color = colorScheme.onPrimaryContainer,
                            textAlign = TextAlign.Center
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = colorScheme.background
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(end = 20.dp)
            )
        }


    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(colorScheme.background)
        ) {
            SettingListItem(
                icon = Icons.Default.Person,
                title = "Account",
                description = "Privacy, security, change email or number",
                onClick = {
                    navController.navigate(Destinations.AccountScreen.route)
                },
                colorScheme = colorScheme
            )
            SettingListItem(
                icon = Icons.Default.Person,
                title = "Chats",
                description = "Theme, wallpapers, chat history",
                onClick = {
                    navController.navigate(Destinations.HomeScreen.route)
                },
                colorScheme = colorScheme
            )
            SettingListItem(
                icon = Icons.Default.Notifications,
                title = "Notifications",
                description = "Message, group & call tones",
                onClick = {
                    // Add navigation logic or other functions here
                },
                colorScheme = colorScheme
            )
            SettingListItem(
                icon = Icons.Default.Person,
                title = "Storage and data",
                description = "Network usage, auto-download",
                onClick = {
                    // Logic for Storage
                },
                colorScheme = colorScheme
            )
            SettingListItem(
                icon = Icons.Default.Person,
                title = "Help",
                description = "Help centre, contact us, privacy policy",
                onClick = {
                    // Additional navigation or logic for Help
                },
                colorScheme = colorScheme
            )

            // logout
            SettingListItem(
                icon = Icons.Default.ExitToApp,
                title = "Logout",
                description = "Logout from app",
                onClick = onSignOut,
                colorScheme = colorScheme
            )
        }
    }
}

@Composable
fun SettingListItem(
    icon: ImageVector,
    title: String,
    description: String,
    onClick: () -> Unit,
    colorScheme: ColorScheme
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(24.dp),
            tint = colorScheme.onBackground
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                color = colorScheme.onBackground
            )
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                color = colorScheme.onBackground
            )
        }
    }
}