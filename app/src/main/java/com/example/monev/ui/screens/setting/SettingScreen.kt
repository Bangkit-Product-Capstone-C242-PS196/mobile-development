package com.example.monev.ui.screens.setting

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.ComponentActivity
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
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
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkRequest
import coil.compose.AsyncImage
import com.example.monev.sign_in.UserData
import com.example.monev.utils.PreferenceManager
import com.example.monev.worker.DailyReminderWorker
import java.util.concurrent.TimeUnit

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingScreen(
    modifier: Modifier = Modifier,
    navController: NavController,
    userData: UserData?,
    onSignOut: () -> Unit
) {
    val colorScheme = MaterialTheme.colorScheme
    val context = LocalContext.current

    val requestPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            showNotification(context)
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
                            text = "SETTING",
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
                .padding(16.dp)
        ) {
            // Profile Header
            ProfileHeader(userData)

            // Setting Section
            SettingSection(context, requestPermissionLauncher)

            // Account Section
            AccountSection(onSignOut)


        }
    }
}

@Composable
fun ProfileHeader(userData: UserData?) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (userData?.profilePictureUrl != null) {
            AsyncImage(
                model = userData.profilePictureUrl,
                contentDescription = "Profile Picture",
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )
        }
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(start = 16.dp)
        ) {
            Text(
                text = userData?.username ?: "Unknown User",
                fontSize = 20.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
fun SettingSection(
    context: Context,
    requestPermissionLauncher: ManagedActivityResultLauncher<String, Boolean>
) {
    val preferenceManager = remember { PreferenceManager(context) }
    var isNotificationEnabled by remember { mutableStateOf(preferenceManager.getNotificationStatus()) }

    fun startDailyReminderWorker() {
        val dailyReminderRequest: WorkRequest = PeriodicWorkRequestBuilder<DailyReminderWorker>(7, TimeUnit.DAYS).build()
        WorkManager.getInstance(context).enqueue(dailyReminderRequest)
    }

    Text(
        text = "Settings",
        color = Color.Gray,
        modifier = Modifier.padding(vertical = 8.dp)
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .padding(end = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(Color.Blue, shape = CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.Notifications,
                    contentDescription = "Notifications",
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = "Enable Notifications",
                fontSize = 16.sp,
                modifier = Modifier.weight(1f)
            )
        }
        Switch(
            checked = isNotificationEnabled,
            onCheckedChange = { isChecked ->
                isNotificationEnabled = isChecked
                preferenceManager.setNotificationStatus(isChecked)
                if (isChecked) {
                    showNotification(context)
                    startDailyReminderWorker()
                }
            }
        )
    }
}


@Composable
fun AccountSection(onSignOut: () -> Unit) {
    TextButton(
        onClick = onSignOut,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = "Log Out",
            color = Color.Red
        )
    }
}


fun showNotification(context: Context) {
    val preferenceManager = PreferenceManager(context)
    val isNotificationEnabled = preferenceManager.getNotificationStatus()

    // Jika notifikasi dimatikan, hentikan fungsi
    if (!isNotificationEnabled) return

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
        .setContentTitle("Notification Enabled")
        .setContentText("This is a test notification.")
        .setPriority(NotificationCompat.PRIORITY_DEFAULT)

    if (ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
        with(NotificationManagerCompat.from(context)) {
            notify(notificationId, builder.build())
        }
    } else {
        ActivityCompat.requestPermissions(context as ComponentActivity, arrayOf(Manifest.permission.POST_NOTIFICATIONS), 1)
    }
}
