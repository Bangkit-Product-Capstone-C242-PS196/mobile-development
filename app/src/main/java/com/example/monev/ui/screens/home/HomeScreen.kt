package com.example.monev.ui.screens.home

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.provider.MediaStore
import android.speech.tts.TextToSpeech
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import com.example.monev.sign_in.UserData

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

    val textToSpeech = remember { TextToSpeech(context) { status ->
        if (status == TextToSpeech.SUCCESS) {
            Log.d("HomeScreen", "TTS Initialized Successfully")
        } else {
            Log.e("HomeScreen", "Failed to initialize TTS")
        }
    } }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        hasCameraPermission = isGranted
        Log.d("HomeScreen", "Camera Permission: $isGranted")
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
                Log.e("HomeScreen", "Failed to capture image: Bitmap is null")
                errorMessage = "Gagal mengambil gambar."
            }
        } else {
            Log.e("HomeScreen", "Image capture failed: Result code is not OK")
            errorMessage = "Pengambilan gambar dibatalkan."
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = "Halaman Beranda",
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onBackground,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier
                        .padding(bottom = 4.dp)
                        .semantics {
                            contentDescription = "Halaman Beranda"
                        }
                )

                Spacer(
                    modifier = Modifier
                        .height(100.dp)
                )

                Button(
                    onClick = {
                        if (hasCameraPermission) {
                            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                            cameraLauncher.launch(intent)
                        } else {
                            permissionLauncher.launch(Manifest.permission.CAMERA)
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                    shape = CircleShape,
                    modifier = Modifier
                        .size(200.dp)
                        .semantics {
                            contentDescription = "Tombol Buka Kamera"
                        },
                    content = {
                        Icon(
                            imageVector = Icons.Filled.Person,
                            contentDescription = "Ikon Kamera",
                            tint = Color.White,
                            modifier = Modifier.size(60.dp)
                        )
                    }
                )

                Spacer(modifier = Modifier.height(24.dp))

                 Button(
            onClick = {
                // Navigasi ke ListHistoryScreen
                navController.navigate("list_history_screen")

       
            }

            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Riwayat History",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onBackground,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier
                        .padding(bottom = 8.dp)
                        .semantics {
                            contentDescription = "Judul Riwayat History"
                        }
                )

                Spacer(modifier = Modifier.height(8.dp))

                Card(
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .clickable {
                            navController.navigate("HistoryScreen")
                        }
                        .semantics {
                            contentDescription = "Card Riwayat History, klik untuk melihat daftar riwayat"
                        },
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(RoundedCornerShape(16.dp))
                    ) {
                        Image(
                            imageVector = Icons.Filled.Person,
                            contentDescription = "Background Riwayat History",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )

                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Color.Black.copy(alpha = 0.4f))
                        )

                        Box(
                            modifier = Modifier
                                .fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "10.000",
                                style = MaterialTheme.typography.displayMedium.copy(
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold
                                ),
                                modifier = Modifier
                                    .semantics {
                                        contentDescription = "Nominal Riwayat: 10.000"
                                    }
                            )
                        }
                    }
                }
            }

            errorMessage?.let {
                Text(
                    text = "Error: $it",
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier
                        .padding(top = 8.dp)
                        .semantics {
                            contentDescription = "Pesan Error"
                        }
                )
            }
        }
    }
}