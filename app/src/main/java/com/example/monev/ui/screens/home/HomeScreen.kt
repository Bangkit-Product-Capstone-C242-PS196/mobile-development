package com.example.monev.ui.screens.home

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.provider.MediaStore
import android.speech.tts.TextToSpeech
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import com.example.monev.helper.PredictionHelper
import com.example.monev.sign_in.UserData
import java.nio.ByteBuffer
import androidx.compose.ui.unit.sp
import java.nio.ByteOrder

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

    // Fungsi untuk memproses gambar yang diambil dan membuat prediksi
    fun processImage(bitmap: Bitmap) {
        // Cek apakah PredictionHelper sudah diinisialisasi dengan benar
        if (predictionHelper != null) {
            val imageByteBuffer = convertBitmapToByteBuffer(bitmap)
            predictionHelper?.predict(imageByteBuffer)
        } else {
            error("Model is not initialized yet.")
        }
    }

    // Request permission untuk akses kamera
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        hasCameraPermission = isGranted
        Log.d("HomeScreen", "Camera Permission: $isGranted")
    }

    // Pemanggilan fungsi ketika gambar dari kamera berhasil diambil
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->

        if (result.resultCode == android.app.Activity.RESULT_OK) {
            val data = result.data

            val imageBitmap: Bitmap? = data?.extras?.get("data") as? Bitmap
            if (imageBitmap != null) {
                processImage(imageBitmap)  // Proses gambar
            } else {
                Log.e("HomeScreen", "Failed to capture image: Bitmap is null")
            }
        } else {
            Log.e("HomeScreen", "Image capture failed: Result code is not OK")
        }
    }

    // Setup PredictionHelper ketika komponen dimulai
    LaunchedEffect(Unit) {
        predictionHelper = PredictionHelper(
            context = context,
            onResult = { predictedClass, confidence ->
                errorMessage = null
                Log.d("HomeScreen", "Prediction result: Kelas $predictedClass dengan confidence: $confidence")

                // Menambahkan TTS setelah hasil prediksi dengan format yang diinginkan
                textToSpeech.speak(
                    "Hasil prediksi adalah. Kelas $predictedClass dengan confidence ${"%.2f".format(confidence * 100)} persen.",
                    TextToSpeech.QUEUE_FLUSH,
                    null,
                    null
                )

                // Panggil callback untuk menavigasi ke ResultScreen
                onPredictionResult(predictedClass, confidence)
            },
            onError = { error ->
                errorMessage = error
                Log.e("HomeScreen", "Prediction error: $error")
            }
        )
    }

    // UI
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Home Screen", color = MaterialTheme.colorScheme.onBackground)

        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = {
                // Navigasi ke CreateHistoryScreen
                navController.navigate("create_history_screen")
            },
            modifier = Modifier.padding(bottom = 8.dp)
        ) {
            Text(text = "Go to Create History")
        }

        Button(
            onClick = {
                // Navigasi ke ListHistoryScreen
                navController.navigate("list_history_screen")
            }
        ) {
            Text(text = "Go to List History")
        }

        // Tombol untuk membuka kamera
        Button(
            onClick = {
                if (hasCameraPermission) {
                    val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                    cameraLauncher.launch(intent)
                } else {
                    permissionLauncher.launch(Manifest.permission.CAMERA)
                }
            },
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
        ) {
            Text(text = "Open Camera")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Tombol untuk sign-out
        Button(onClick = onSignOut) {
            Text(text = "Signout")
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Tampilkan pesan error jika ada
        errorMessage?.let {
            Text(text = "Error: $it", color = MaterialTheme.colorScheme.error)
        }
    }
}

// Fungsi untuk mengonversi Bitmap ke ByteBuffer
fun convertBitmapToByteBuffer(bitmap: Bitmap): ByteBuffer {
    val inputSize = 224  // Ukuran gambar input
    val byteBuffer = ByteBuffer.allocateDirect(4 * inputSize * inputSize * 3)
    byteBuffer.order(ByteOrder.nativeOrder())

    val scaledBitmap = Bitmap.createScaledBitmap(bitmap, inputSize, inputSize, false)
    for (i in 0 until inputSize) {
        for (j in 0 until inputSize) {
            val pixel = scaledBitmap.getPixel(i, j)
            byteBuffer.putFloat(((pixel shr 16) and 0xFF) / 255.0f)
            byteBuffer.putFloat(((pixel shr 8) and 0xFF) / 255.0f)
            byteBuffer.putFloat((pixel and 0xFF) / 255.0f)
        }
    }

    return byteBuffer
}
