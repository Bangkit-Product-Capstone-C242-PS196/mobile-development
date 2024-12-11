package com.example.monev.ui.screens.result

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
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.focused
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.monev.data.repository.PredictionRepository
import com.example.monev.ui.screens.home.ImageHolder
import com.example.monev.viewmodel.history.HistoryViewModel
import kotlinx.coroutines.launch

@Composable
fun ResultScreen(
    navController: NavController,
    predictionResult: String,
    confidence: Float
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val predictionRepository = remember { PredictionRepository() }

    val historyViewModel: HistoryViewModel = viewModel()

    var currentPredictionResult by remember { mutableStateOf(predictionResult) }
    var currentConfidence by remember { mutableStateOf(confidence) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }

    val hasCameraPermission = remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        hasCameraPermission.value = isGranted
        if (!isGranted) {
            Toast.makeText(context, "Izin kamera ditolak.", Toast.LENGTH_SHORT).show()
        }
    }

    val textToSpeech = remember {
        TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                Log.d("ResultScreen", "TTS Initialized Successfully")
            } else {
                Log.e("ResultScreen", "Failed to initialize TTS")
            }
        }
    }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == android.app.Activity.RESULT_OK) {
            val data = result.data
            val imageBitmap: Bitmap? = data?.extras?.get("data") as? Bitmap
            if (imageBitmap != null) {
                isLoading = true
                coroutineScope.launch {
                    val predictionResult = predictionRepository.predictImage(imageBitmap)
                    predictionResult.fold(
                        onSuccess = { response ->
                            Log.d("MY_APP_PREDICT", "Response sukses (scan ulang): $response")
                            if (response.status == "success") {
                                val newScore = response.data.result.score.toString()
                                val newConfidence = response.data.result.confidence
                                currentPredictionResult = newScore
                                currentConfidence = newConfidence
                                isLoading = false

                                // Tambah ke history
                                historyViewModel.addHistory(newScore, newConfidence)

                                textToSpeech.speak(
                                    "Hasil prediksi adalah nominal $newScore dengan confidence ${"%.2f".format(newConfidence * 100)} persen.",
                                    TextToSpeech.QUEUE_FLUSH,
                                    null,
                                    null
                                )
                            } else {
                                isLoading = false
                                errorMessage = response.message
                            }
                        },
                        onFailure = { exception ->
                            isLoading = false
                            Log.e("MY_APP_PREDICT", "Error (scan ulang): ${exception.message}")
                            errorMessage = exception.message ?: "Unknown error"
                        }
                    )
                }
            } else {
                Log.e("ResultScreen", "Failed to capture image: Bitmap is null")
                errorMessage = "Gagal mengambil gambar."
            }
        } else {
            Log.e("ResultScreen", "Image capture failed: Result code is not OK")
            errorMessage = "Pengambilan gambar dibatalkan."
        }
    }

    // Prediksi awal ketika layar dibuka, jika ImageHolder.lastImage ada
    LaunchedEffect(Unit) {
        val imageBitmap = ImageHolder.lastImage
        if (imageBitmap != null) {
            isLoading = true
            coroutineScope.launch {
                val predictionResult = predictionRepository.predictImage(imageBitmap)
                predictionResult.fold(
                    onSuccess = { response ->
                        Log.d("MY_APP_PREDICT", "Response sukses (awal): $response")
                        if (response.status == "success") {
                            val newScore = response.data.result.score.toString()
                            val newConfidence = response.data.result.confidence
                            currentPredictionResult = newScore
                            currentConfidence = newConfidence
                            isLoading = false

                            // Tambah ke history
                            historyViewModel.addHistory(newScore, newConfidence)

                            textToSpeech.speak(
                                "Hasil prediksi adalah nominal $newScore dengan confidence ${"%.2f".format(newConfidence * 100)} persen.",
                                TextToSpeech.QUEUE_FLUSH,
                                null,
                                null
                            )
                        } else {
                            isLoading = false
                            errorMessage = response.message
                        }
                    },
                    onFailure = { exception ->
                        isLoading = false
                        Log.e("MY_APP_PREDICT", "Error (awal): ${exception.message}")
                        errorMessage = exception.message ?: "Unknown error"
                    }
                )
            }
        } else {
            // Jika tidak ada image, gunakan yang dibawa dari argumen (placeholder)
        }
    }

    Scaffold(
        content = { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
                    .padding(paddingValues)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .shadow(8.dp, RoundedCornerShape(16.dp)),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    ) {
                        Column(
                            modifier = Modifier
                                .padding(24.dp)
                                .fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = "",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier
                                    .size(64.dp)
                                    .semantics { focused = false }
                            )

                            Text(
                                text = "Nominal",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = currentPredictionResult,
                                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.semantics { contentDescription = "Nominal Prediksi: $currentPredictionResult" }
                            )

                            Text(
                                text = "Confidence",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "${"%.2f".format(currentConfidence * 100)}%",
                                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    Button(
                        onClick = {
                            if (hasCameraPermission.value) {
                                val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                                cameraLauncher.launch(intent)
                            } else {
                                permissionLauncher.launch(Manifest.permission.CAMERA)
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Scan Ulang",
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = "Scan Ulang", fontSize = 18.sp, fontWeight = FontWeight.Medium)
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Button(
                        onClick = {
                            navController.navigate("HomeScreen") {
                                popUpTo("HomeScreen") { inclusive = true }
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Home,
                            contentDescription = "Home",
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = "Kembali ke Home", fontSize = 18.sp, fontWeight = FontWeight.Medium)
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    errorMessage?.let { error ->
                        AlertDialog(
                            onDismissRequest = { errorMessage = null },
                            confirmButton = {
                                TextButton(onClick = { errorMessage = null }) {
                                    Text("OK")
                                }
                            },
                            title = {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Warning,
                                        contentDescription = "Error",
                                        tint = MaterialTheme.colorScheme.error,
                                        modifier = Modifier.size(24.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Terjadi Kesalahan")
                                }
                            },
                            text = { Text(error) },
                            containerColor = MaterialTheme.colorScheme.errorContainer,
                            titleContentColor = MaterialTheme.colorScheme.onErrorContainer,
                            textContentColor = MaterialTheme.colorScheme.onErrorContainer
                        )
                    }
                }

                // Overlay gelap dan loading
                if (isLoading) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Black.copy(alpha = 0.5f)),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = Color.White)
                    }
                }
            }
        }
    )
}
