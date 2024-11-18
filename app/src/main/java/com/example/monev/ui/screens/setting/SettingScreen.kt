package com.example.monev.ui.screens.setting

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.monev.ui.navigation.Destinations

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingScreen(
    modifier: Modifier = Modifier,
    navController: NavController,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings", style = MaterialTheme.typography.titleLarge) }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            SettingListItem(
                icon = Icons.Default.Person,
                title = "Account",
                description = "Privacy, security, change email or number",
                onClick = {
                    // Navigasi ke AccountScreen
                    navController.navigate(Destinations.AccountScreen.route)
                }
            )
            SettingListItem(
                icon = Icons.Default.Person,
                title = "Chats",
                description = "Theme, wallpapers, chat history",
                onClick = {
                    // Navigasi ke ChatScreen (buat jika ada)
                    navController.navigate(Destinations.HomeScreen.route)
                }
            )
            SettingListItem(
                icon = Icons.Default.Notifications,
                title = "Notifications",
                description = "Message, group & call tones",
                onClick = {
                    // Tambahkan logika navigasi atau fungsi lain di sini
                }
            )
            SettingListItem(
                icon = Icons.Default.Person,
                title = "Storage and data",
                description = "Network usage, auto-download",
                onClick = {
                    // Logika untuk Storage
                }
            )
            SettingListItem(
                icon = Icons.Default.Person,
                title = "Help",
                description = "Help centre, contact us, privacy policy",
                onClick = {
                    // Navigasi atau logika tambahan untuk Help
                }
            )
        }
    }
}

@Composable
fun SettingListItem(
    icon: ImageVector,
    title: String,
    description: String,
    onClick: () -> Unit
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
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold)
            )
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp)
            )
        }
    }
}