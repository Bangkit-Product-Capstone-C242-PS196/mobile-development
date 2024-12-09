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
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.graphics.vector.ImageVector
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
import coil.compose.AsyncImage
import com.example.monev.sign_in.UserData
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
    userData: UserData?,
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
                .padding(16.dp)
        ) {
            // Profile Header
            ProfileHeader(userData)

            // Status Section
//            StatusSection()


            // Dashboard Section
            DashboardSection(context, requestPermissionLauncher)

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
            Text(
                text = "Senior Designer",
                color = Color.Gray
            )
        }
        IconButton(onClick = { /* Edit profile */ }) {
            Icon(
                Icons.Default.Edit,
                contentDescription = "Edit Profile",
                tint = Color.Gray
            )
        }
    }
}

//@Composable
//fun StatusSection() {
//    Text(
//        text = "My Status",
//        color = Color.Gray,
//        modifier = Modifier.padding(vertical = 8.dp)
//    )
//    Row(
//        horizontalArrangement = Arrangement.spacedBy(8.dp)
//    ) {
//        StatusChip(
//            text = "Away",
//            icon = Icons.Default.Mood,
//            backgroundColor = Color.Black,
//            textColor = Color.White
//        )
//        StatusChip(
//            text = "At Work",
//            icon = Icons.Default.Work,
//            backgroundColor = Color(0xFFE6F3E6),
//            textColor = Color(0xFF2E7D32)
//        )
//        StatusChip(
//            text = "Gaming",
//            icon = Icons.Default.SportsEsports,
//            backgroundColor = Color(0xFFFFF3E0),
//            textColor = Color(0xFFFF9800)
//        )
//    }
//}

@Composable
fun NotificationConfirmationDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = "Enable Notification") },
        text = { Text(text = "Do you want to enable notifications?") },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text("Yes")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("No")
            }
        }
    )
}

@Composable
fun DashboardSection(
    context: Context,
    requestPermissionLauncher: ManagedActivityResultLauncher<String, Boolean>
) {
    var showDialog by remember { mutableStateOf(false) }

    if (showDialog) {
        NotificationConfirmationDialog(
            onConfirm = {
                showDialog = false
                if (ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
                    showDummyNotification(context)
                } else {
                    requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                }
            },
            onDismiss = { showDialog = false }
        )
    }

    Text(
        text = "Dashboard",
        color = Color.Gray,
        modifier = Modifier.padding(vertical = 8.dp)
    )
    DashboardItem(
        icon = Icons.Default.ShoppingCart,
        iconBackgroundColor = Color.Green,
        text = "Payments",
        badge = "2 New"
    )
    DashboardItem(
        icon = Icons.Default.CheckCircle,
        iconBackgroundColor = Color.Yellow,
        text = "Achievements"
    )
    DashboardItem(
        icon = Icons.Default.Lock,
        iconBackgroundColor = Color.Gray,
        text = "Privacy",
        badge = "Actions Needed",
        badgeColor = Color.Red
    )
    DashboardItem(
        icon = Icons.Default.Notifications,
        iconBackgroundColor = Color.Blue,
        text = "Notifications",
        badge = "5 New",
        badgeColor = Color.Magenta,
        onClick = { showDialog = true }
    )
}

@Composable
fun DashboardItem(
    icon: ImageVector,
    iconBackgroundColor: Color,
    text: String,
    badge: String? = null,
    badgeColor: Color = Color.Blue,
    onClick: (() -> Unit)? = null
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable(onClick = onClick ?: {}),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(iconBackgroundColor, shape = CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    icon,
                    contentDescription = text,
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = text,
                fontWeight = FontWeight.Medium
            )
        }

        badge?.let {
            Surface(
                color = badgeColor,
                shape = MaterialTheme.shapes.large
            ) {
                Text(
                    text = it,
                    color = Color.White,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
        }
    }
}

@Composable
fun AccountSection(onSignOut: () -> Unit) {
    Text(
        text = "My Account",
        color = Color.Gray,
        modifier = Modifier.padding(vertical = 8.dp)
    )
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

@Composable
fun StatusChip(
    text: String,
    icon: ImageVector,
    backgroundColor: Color,
    textColor: Color
) {
    Surface(
        color = backgroundColor,
        shape = MaterialTheme.shapes.large,
        modifier = Modifier.height(40.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                icon,
                contentDescription = text,
                tint = textColor,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = text,
                color = textColor,
                fontSize = 14.sp
            )
        }
    }
}

