package com.example.monev.ui.screens.setting

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import com.example.monev.ui.navigation.Destinations

fun showDummyNotification(context: Context) {
    val channelId = "dummy_channel_id"
    val channelName = "Dummy Notifications"
    val notificationId = 1

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val channel = NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_DEFAULT).apply {
            description = "Channel for dummy notifications"
        }
        val notificationManager: NotificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }

    val builder = NotificationCompat.Builder(context, channelId)
        .setSmallIcon(android.R.drawable.ic_dialog_info)
        .setContentTitle("Dummy Notification")
        .setContentText("This is a dummy notification.")
        .setPriority(NotificationCompat.PRIORITY_DEFAULT)

    if (ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
        with(NotificationManagerCompat.from(context)) {
            notify(notificationId, builder.build())
        }
    } else {
        ActivityCompat.requestPermissions(context as ComponentActivity, arrayOf(Manifest.permission.POST_NOTIFICATIONS), 1)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingScreen(
    modifier: Modifier = Modifier,
    navController: NavController,
    onSignOut: () -> Unit
) {
    val colorScheme = MaterialTheme.colorScheme
    val context = LocalContext.current

    val requestPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            showDummyNotification(context)
        }
    }

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
                    if (ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
                        showDummyNotification(context)
                    } else {
                        requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                    }
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