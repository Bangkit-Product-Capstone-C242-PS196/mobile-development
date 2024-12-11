package com.example.monev.ui.screens.home

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.provider.MediaStore
import android.speech.tts.TextToSpeech
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.monev.sign_in.UserData
import com.example.monev.R

object ImageHolder {
    var lastImage: Bitmap? = null
}

@Composable
fun HomeScreen(
    navController: NavController,
    userData: UserData? = null,
    onSignOut: () -> Unit,
    onPredictionResult: (String, Float) -> Unit
) {
    val context = LocalContext.current
    var hasCameraPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
        )
    }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    // Animation states
    val headerAlpha by animateFloatAsState(targetValue = 1f, animationSpec = tween(1000))
    val contentScale by animateFloatAsState(targetValue = 1f, animationSpec = spring())
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        hasCameraPermission = isGranted
        if (!isGranted) {
            Toast.makeText(context, "Izin kamera ditolak.", Toast.LENGTH_SHORT).show()
        }
    }
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == android.app.Activity.RESULT_OK) {
            val data = result.data
            val imageBitmap: Bitmap? = data?.extras?.get("data") as? Bitmap
            if (imageBitmap != null) {
                ImageHolder.lastImage = imageBitmap
                navController.navigate("ResultScreen/Unknown/0.0f")
            } else {
                errorMessage = "Gagal mengambil gambar."
            }
        } else {
            errorMessage = "Pengambilan gambar dibatalkan."
        }
    }
    val gradientBrush = Brush.verticalGradient(
        colors = listOf(
            MaterialTheme.colorScheme.primary,
            MaterialTheme.colorScheme.primary.copy(alpha = 0.8f)
        )
    )
    MaterialTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
            // Background gradient
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp)
                    .background(
                        brush = gradientBrush,
                        shape = RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp)
                    )
            )
            // Main content
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp)
                    .alpha(headerAlpha)
            ) {
                // Header
                HeaderSection(userData?.username)

                Spacer(modifier = Modifier.height(48.dp))
                // Camera Section
                CameraSection(
                    hasCameraPermission = hasCameraPermission,
                    onCameraClick = {
                        if (hasCameraPermission) {
                            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                            cameraLauncher.launch(intent)
                        } else {
                            permissionLauncher.launch(Manifest.permission.CAMERA)
                        }
                    },
                    contentScale = contentScale
                )
                Spacer(modifier = Modifier.height(24.dp))
                // History Section
                HistorySection(
                    onHistoryClick = { navController.navigate("HistoryScreen") },
                    contentScale = contentScale
                )
                // Error message
                errorMessage?.let {
                    ErrorMessage(message = it)
                }
            }
        }
    }
}
@Composable
private fun HeaderSection(username: String?) {
    Column(
        modifier = Modifier.padding(vertical = 16.dp)
    ) {
        Text(
            text = "Halo, ${username ?: "Pengguna"}",
            style = MaterialTheme.typography.headlineMedium.copy(
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.sp
            ),
            color = Color.White
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Selamat datang di Monev!",
            style = MaterialTheme.typography.bodyLarge.copy(
                color = Color.White.copy(alpha = 0.8f)
            )
        )
    }
}
@Composable
private fun CameraSection(
    hasCameraPermission: Boolean,
    onCameraClick: () -> Unit,
    contentScale: Float
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(280.dp)
            .scale(contentScale)
            .clip(RoundedCornerShape(24.dp))
            .background(
                MaterialTheme.colorScheme.surface,
                RoundedCornerShape(24.dp)
            )
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.1f),
                shape = RoundedCornerShape(24.dp)
            )
            .clickable(onClick = onCameraClick)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .background(
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                        CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.AddCircle,
                    contentDescription = "Camera",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(36.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Ambil Foto",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )

            Text(
                text = "Ketuk untuk memulai",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
        }
    }
}
@Composable
private fun HistorySection(
    onHistoryClick: () -> Unit,
    contentScale: Float
) {
    Column {
        Text(
            text = "Riwayat Scan",
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.SemiBold
            ),
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(modifier = Modifier.height(16.dp))

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(160.dp)
                .scale(contentScale)
                .clickable(onClick = onHistoryClick),
            shape = RoundedCornerShape(24.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Box(
                modifier = Modifier.fillMaxSize()
            ) {
                // Background image with scrim
                AsyncImage(
                    model = R.drawable.welcome,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color.Black.copy(alpha = 0.3f),
                                    Color.Black.copy(alpha = 0.7f)
                                )
                            )
                        )
                )

                // Content
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "10.000",
                        style = MaterialTheme.typography.displayMedium.copy(
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    )

                    Text(
                        text = "Total Scan",
                        style = MaterialTheme.typography.titleMedium.copy(
                            color = Color.White.copy(alpha = 0.8f)
                        )
                    )
                }
            }
        }
    }
}
@Composable
private fun ErrorMessage(message: String) {
    Spacer(modifier = Modifier.height(16.dp))
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.errorContainer,
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Filled.Close,
                contentDescription = "Error",
                tint = MaterialTheme.colorScheme.error
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onErrorContainer
            )
        }
    }
}