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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.monev.R
import com.example.monev.helper.PredictionHelper
import com.example.monev.sign_in.UserData
import com.example.monev.viewmodel.history.HistoryViewModel
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.util.Calendar

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

    var predictionHelper by remember { mutableStateOf<PredictionHelper?>(null) }

    // Setup TextToSpeech
    val textToSpeech = remember { TextToSpeech(context) { status ->
        if (status == TextToSpeech.SUCCESS) {
            Log.d("HomeScreen", "TTS Initialized Successfully")
        } else {
            Log.e("HomeScreen", "Failed to initialize TTS")
        }
    } }

    // Function to process captured image and make prediction
    fun processImage(bitmap: Bitmap) {
        if (predictionHelper != null) {
            val imageByteBuffer = convertBitmapToByteBuffer(bitmap)
            predictionHelper?.predict(imageByteBuffer)
        } else {
            error("Model is not initialized yet.")
        }
    }

    // Request permission for camera access
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        hasCameraPermission = isGranted
        Log.d("HomeScreen", "Camera Permission: $isGranted")
        if (!isGranted) {
            Toast.makeText(context, "Izin kamera ditolak.", Toast.LENGTH_SHORT).show()
        }
    }

    // Handle camera result
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->

        if (result.resultCode == android.app.Activity.RESULT_OK) {
            val data = result.data
            val imageBitmap: Bitmap? = data?.extras?.get("data") as? Bitmap
            if (imageBitmap != null) {
                processImage(imageBitmap)
            } else {
                Log.e("HomeScreen", "Failed to capture image: Bitmap is null")
                errorMessage = "Gagal mengambil gambar."
            }
        } else {
            Log.e("HomeScreen", "Image capture failed: Result code is not OK")
            errorMessage = "Pengambilan gambar dibatalkan."
        }
    }

    // Get HistoryViewModel
    val historyViewModel: HistoryViewModel = viewModel()

    // Setup PredictionHelper when the component is launched
    LaunchedEffect(Unit) {
        predictionHelper = PredictionHelper(
            context = context,
            onResult = { predictedClass, confidence ->
                errorMessage = null
                Log.d("HomeScreen", "Prediction result: Kelas $predictedClass dengan confidence: $confidence")

                // Mapping class to nominal
                val nominal = getNominal(predictedClass)

                // Use TTS to speak the result
                textToSpeech.speak(
                    "Hasil prediksi adalah. Nominal $nominal dengan confidence ${"%.2f".format(confidence * 100)} persen.",
                    TextToSpeech.QUEUE_FLUSH,
                    null,
                    null
                )

                // Call callback to navigate to ResultScreen
                Log.d("HomeScreen", "Memanggil onPredictionResult dengan kelas: $predictedClass dan confidence: $confidence")
                onPredictionResult(predictedClass, confidence)

                // Add history to Firestore and Room
                historyViewModel.addHistory(nominal, confidence)
            },
            onError = { error ->
                errorMessage = error
                Log.e("HomeScreen", "Prediction error: $error")
            }
        )
    }

    val greetingText = getGreetingText(userData?.username ?: "Unknown User")

    // UI
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
            // Top Section
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.weight(1f)
            ) {
                // Greeting Texts
                Text(
                    text = greetingText.greeting,
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onBackground,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier
                        .padding(bottom = 4.dp)
                        .semantics {
                            contentDescription = greetingText.greeting
                        }
                )

                Text(
                    text = greetingText.username,
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onBackground,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .padding(bottom = 24.dp)
                        .semantics {
                            contentDescription = "Nama Pengguna: ${greetingText.username}"
                        }
                )

                Spacer(
                    modifier = Modifier
                        .height(100.dp)
                )

                // Camera Button
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
                            imageVector = ImageVector.vectorResource(id = R.drawable.ic_camera),
                            contentDescription = "Ikon Kamera",
                            tint = Color.White,
                            modifier = Modifier.size(60.dp)
                        )
                    }
                )

                Spacer(modifier = Modifier.height(24.dp))
            }

            // Bottom Section - History Card
            Column(
//                horizontalAlignment = Alignment.CenterHorizontally,
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
                            // Navigate to ListHistoryScreen when Card is clicked
                            navController.navigate("list_history_screen")
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
                        // Background Image
                        Image(
                            painter = painterResource(id = R.drawable.img_calendar),
                            contentDescription = "Background Riwayat History",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )



                        // Semi-Transparent Overlay
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Color.Black.copy(alpha = 0.4f))
                        )

                        // Nominal Text at Center
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

            // Show error message if any
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

/**
 * Data class to hold greeting text
 */
data class GreetingText(val greeting: String, val username: String)

/**
 * Function to get greeting based on current time and username.
 */
fun getGreetingText(username: String): GreetingText {
    val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
    val greeting = when (hour) {
        in 5..11 -> "Selamat Pagi"
        in 12..14 -> "Selamat Siang"
        in 15..17 -> "Selamat Sore"
        else -> "Selamat Malam"
    }
    return GreetingText(greeting = greeting, username = username)
}

/**
 * Function to map class index to nominal value.
 */
fun getNominal(classIndex: String): String {
    return when (classIndex.toIntOrNull()) {
        0 -> "1.000"
        1 -> "2.000"
        2 -> "5.000"
        3 -> "10.000"
        4 -> "20.000"
        5 -> "50.000"
        6 -> "100.000"
        else -> "Unknown"
    }
}

/**
 * Function to convert Bitmap to ByteBuffer for model prediction.
 */
fun convertBitmapToByteBuffer(bitmap: Bitmap): ByteBuffer {
    val inputSize = 224
    val byteBuffer = ByteBuffer.allocateDirect(4 * inputSize * inputSize * 3)
    byteBuffer.order(ByteOrder.nativeOrder())

    val scaledBitmap = Bitmap.createScaledBitmap(bitmap, inputSize, inputSize, false)
    for (y in 0 until inputSize) {
        for (x in 0 until inputSize) {
            val pixel = scaledBitmap.getPixel(x, y)
            byteBuffer.putFloat(((pixel shr 16) and 0xFF) / 255.0f)  // R
            byteBuffer.putFloat(((pixel shr 8) and 0xFF) / 255.0f)   // G
            byteBuffer.putFloat((pixel and 0xFF) / 255.0f)           // B
        }
    }

    return byteBuffer
}
