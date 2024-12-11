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
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Notifications
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material.icons.rounded.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
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
    // Existing code (variable declarations and logic)
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

    val animatedConfidence by animateFloatAsState(
        targetValue = currentConfidence,
        animationSpec = tween(1000, easing = FastOutSlowInEasing)
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.background,
                        MaterialTheme.colorScheme.background.copy(alpha = 0.95f)
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
                .statusBarsPadding(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Header
            Text(
                text = "Hasil Prediksi",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold,
                    letterSpacing = (-0.5).sp
                ),
                color = MaterialTheme.colorScheme.onBackground
            )

            // Result Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .animateContentSize()
                    .shadow(
                        elevation = 8.dp,
                        shape = RoundedCornerShape(28.dp),
                        spotColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                    ),
                shape = RoundedCornerShape(28.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f)
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(24.dp)
                ) {
                    // Result Icon with Animation
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .background(
                                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                                shape = CircleShape
                            )
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.CheckCircle,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(48.dp)
                        )
                    }

                    // Nominal Section
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "NOMINAL",
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.primary,
                            letterSpacing = 1.sp
                        )
                        Text(
                            text = "Rp $currentPredictionResult",
                            style = MaterialTheme.typography.displaySmall.copy(
                                fontWeight = FontWeight.Bold
                            ),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    // Confidence Section with Circular Progress
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Text(
                            text = "Tingkat Kepercayaan",
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.8f)
                        )

                        Box(contentAlignment = Alignment.Center) {
                            CircularProgressIndicator(
                                progress = animatedConfidence,
                                modifier = Modifier.size(120.dp),
                                strokeWidth = 8.dp,
                                trackColor = MaterialTheme.colorScheme.surfaceVariant,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = "${"%.1f".format(currentConfidence * 100)}%",
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.Bold
                                ),
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // Action Buttons
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
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
                        .height(56.dp)
                        .shadow(4.dp, RoundedCornerShape(16.dp)),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Refresh,
                            contentDescription = null,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "Scan Ulang",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.SemiBold
                            )
                        )
                    }
                }

                OutlinedButton(
                    onClick = {
                        navController.navigate("HomeScreen") {
                            popUpTo("HomeScreen") { inclusive = true }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(
                        1.dp,
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
                    )
                ) {
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Home,
                            contentDescription = null,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "Kembali ke Home",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.SemiBold
                            )
                        )
                    }
                }
            }
        }

        // Error Dialog with modern styling
        errorMessage?.let { error ->
            AlertDialog(
                onDismissRequest = { errorMessage = null },
                shape = RoundedCornerShape(24.dp),
                containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.95f),
                icon = {
                    Icon(
                        imageVector = Icons.Rounded.Warning,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.error
                    )
                },
                title = {
                    Text(
                        text = "Terjadi Kesalahan",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold
                        )
                    )
                },
                text = {
                    Text(
                        text = error,
                        style = MaterialTheme.typography.bodyLarge
                    )
                },
                confirmButton = {
                    TextButton(
                        onClick = { errorMessage = null },
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = "OK",
                            style = MaterialTheme.typography.labelLarge.copy(
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                }
            )
        }

        // Loading Overlay with blur effect
        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.3f))
                    .blur(8.dp),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.size(48.dp),
                    color = MaterialTheme.colorScheme.primary,
                    strokeWidth = 4.dp
                )
            }
        }
    }
}