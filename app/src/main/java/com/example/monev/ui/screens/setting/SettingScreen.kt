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
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.Notifications
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
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
import com.example.monev.ui.navigation.Destinations
import com.example.monev.utils.PreferenceManager
import com.example.monev.worker.DailyReminderWorker
import java.util.concurrent.TimeUnit

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingScreen(
    modifier: Modifier = Modifier,
    userData: UserData?,
    onSignOut: () -> Unit,
    navController: NavController
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

    Divider(
        modifier = Modifier.padding(horizontal = 16.dp),
        color = colorScheme.outlineVariant.copy(alpha = 0.5f)
    )

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "PENGATURAN",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                        color = colorScheme.onSurface
                    )
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = colorScheme.background,
                    scrolledContainerColor = colorScheme.background
                ),
                modifier = Modifier.statusBarsPadding()
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(colorScheme.background)
                .verticalScroll(rememberScrollState())
        ) {
            // Profile Header
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .padding(top = 8.dp),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(
                    containerColor = colorScheme.primaryContainer.copy(alpha = 0.5f)
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                ProfileHeader(userData)
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Settings Section
            Column(
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .fillMaxWidth()
            ) {
                Text(
                    text = "PREFERENSI",
                    style = MaterialTheme.typography.labelLarge,
                    color = colorScheme.primary,
                    modifier = Modifier.padding(bottom = 16.dp),
                    letterSpacing = 1.sp
                )

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = colorScheme.surface
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    SettingSection(context, requestPermissionLauncher, navController)
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Account Section
            Column(
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .fillMaxWidth()
            ) {
                Text(
                    text = "AKUN",
                    style = MaterialTheme.typography.labelLarge,
                    color = colorScheme.primary,
                    modifier = Modifier.padding(bottom = 16.dp),
                    letterSpacing = 1.sp
                )

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = colorScheme.surface
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    AccountSection(onSignOut)
                }
            }
        }
    }
}

@Composable
fun ProfileHeader(
    userData: UserData?,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Profile Picture
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surfaceVariant)
            ) {
                if (userData?.profilePictureUrl != null) {
                    AsyncImage(
                        model = userData.profilePictureUrl,
                        contentDescription = "Profile Picture",
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(CircleShape)
                            .border(
                                width = 2.dp,
                                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f),
                                shape = CircleShape
                            ),
                        contentScale = ContentScale.Crop,
                        error = ColorPainter(MaterialTheme.colorScheme.surfaceVariant)
                    )
                } else {
                    // Default icon when no profile picture
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "Default Profile Picture",
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp)
                            .clip(CircleShape),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // User Information
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 16.dp)
            ) {
                Text(
                    text = userData?.username ?: "Unknown User",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
fun SettingSection(
    context: Context,
    requestPermissionLauncher: ManagedActivityResultLauncher<String, Boolean>,
    navController: NavController
) {
    val preferenceManager = remember { PreferenceManager(context) }
    var isNotificationEnabled by remember { mutableStateOf(preferenceManager.getNotificationStatus()) }
    val colorScheme = MaterialTheme.colorScheme

    fun startDailyReminderWorker() {
        val dailyReminderRequest: WorkRequest = PeriodicWorkRequestBuilder<DailyReminderWorker>(7, TimeUnit.DAYS).build()
        WorkManager.getInstance(context).enqueue(dailyReminderRequest)
    }

    Column(modifier = Modifier.fillMaxWidth()) {
        // Notifications
        ListItem(
            headlineContent = {
                Text(
                    text = "Nyalakan Notifikasi",
                    style = MaterialTheme.typography.bodyLarge
                )
            },
            leadingContent = {
                Icon(
                    imageVector = Icons.Rounded.Notifications,
                    contentDescription = "Notifikasi",
                    tint = colorScheme.primary,
                    modifier = Modifier
                        .background(
                            color = colorScheme.primary.copy(alpha = 0.1f),
                            shape = CircleShape
                        )
                        .padding(8.dp)
                )
            },
            trailingContent = {
                Switch(
                    checked = isNotificationEnabled,
                    onCheckedChange = { isChecked ->
                        isNotificationEnabled = isChecked
                        preferenceManager.setNotificationStatus(isChecked)
                        if (isChecked) {
                            showNotification(context)
                            startDailyReminderWorker()
                        }
                    },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = colorScheme.secondary,
                        checkedTrackColor = colorScheme.secondaryContainer,
                        uncheckedThumbColor = colorScheme.onSurface,
                        uncheckedTrackColor = colorScheme.surface
                    ),
                    modifier = Modifier.semantics {
                        contentDescription = if (isNotificationEnabled) {
                            "Notifikasi sedang aktif. klik untuk menonaktifkan"
                        } else {
                            "Notifikasi mati. klik untuk mengaktifkan"
                        }
                    }
                )
            }
        )

        Divider(
            modifier = Modifier.padding(horizontal = 16.dp),
            color = colorScheme.outlineVariant.copy(alpha = 0.5f)
        )

        // About Us
        ListItem(
            headlineContent = {
                Text(
                    text = "Tentang Kami",
                    style = MaterialTheme.typography.bodyLarge
                )
            },
            leadingContent = {
                Icon(
                    imageVector = Icons.Rounded.Info,
                    contentDescription = "Tentang Kami",
                    tint = colorScheme.primary,
                    modifier = Modifier
                        .background(
                            color = colorScheme.primary.copy(alpha = 0.1f),
                            shape = CircleShape
                        )
                        .padding(8.dp)
                )
            },
            modifier = Modifier.clickable {
                navController.navigate(Destinations.AboutScreen.route)
            }
        )
    }
}

@Composable
fun AccountSection(onSignOut: () -> Unit) {
    ListItem(
        headlineContent = {
            Text(
                text = "Keluar",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.error
            )
        },
        leadingContent = {
            Icon(
                imageVector = Icons.Rounded.Close,
                contentDescription = "Keluar",
                tint = MaterialTheme.colorScheme.error,
                modifier = Modifier
                    .background(
                        color = MaterialTheme.colorScheme.error.copy(alpha = 0.1f),
                        shape = CircleShape
                    )
                    .padding(8.dp)
            )
        },
        modifier = Modifier.clickable(onClick = onSignOut)
    )
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