package com.example.monev.ui.screens.result

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
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.monev.helper.PredictionHelper
import com.example.monev.ui.navigation.Destinations
import com.example.monev.viewmodel.history.HistoryViewModel
import java.nio.ByteBuffer
import java.nio.ByteOrder

@Composable
fun ResultScreen(
    navController: NavController,
    predictionResult: String,
    confidence: Float,
    viewModel: HistoryViewModel = viewModel()
) {
    val context = LocalContext.current

    // State variables untuk menyimpan hasil prediksi dan confidence
    var currentNominal by remember { mutableStateOf(getNominal(predictionResult)) }
    var currentConfidence by remember { mutableStateOf(confidence) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    // State untuk PredictionHelper
    var predictionHelper by remember { mutableStateOf<PredictionHelper?>(null) }

    // Setup TextToSpeech
    val textToSpeech = remember {
        TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                Log.d("ResultScreen", "TTS Initialized Successfully")
            } else {
                Log.e("ResultScreen", "Failed to initialize TTS")
            }
        }
    }

    // Fungsi untuk memproses gambar yang diambil dan membuat prediksi
    fun processImage(bitmap: Bitmap) {
        predictionHelper?.predict(convertBitmapToByteBuffer(bitmap))
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
                errorMessage = "Gagal mengambil gambar."
                Log.e("ResultScreen", "Bitmap is null")
            }
        } else {
            errorMessage = "Pengambilan gambar dibatalkan."
            Log.e("ResultScreen", "Image capture failed: Result code is not OK")
        }
    }

    // Request permission untuk akses kamera
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            // Izin diberikan, buka kamera
            cameraLauncher.launch(Intent(MediaStore.ACTION_IMAGE_CAPTURE))
        } else {
            errorMessage = "Izin kamera ditolak."
            Log.e("ResultScreen", "Camera permission denied")
        }
    }



    // Setup PredictionHelper ketika komponen dimulai
    LaunchedEffect(Unit) {
        predictionHelper = PredictionHelper(
            context = context,
            onResult = { predictedClass, confidenceValue ->
                val nominal = getNominal(predictedClass)
                currentNominal = nominal
                currentConfidence = confidenceValue
                errorMessage = null
                Log.d("ResultScreen", "Prediction result: Nominal $nominal dengan confidence: $confidenceValue")

                // Menambahkan TTS setelah hasil prediksi dengan format yang diinginkan
                textToSpeech.speak(
                    "Hasil prediksi adalah. Nominal $nominal dengan confidence ${"%.2f".format(confidenceValue * 100)} persen.",
                    TextToSpeech.QUEUE_FLUSH,
                    null,
                    null
                )

                // Menambahkan history ke Firestore dan Room
                viewModel.addHistory(nominal, confidenceValue)
            },
            onError = { error ->
                errorMessage = error
                Log.e("ResultScreen", "Prediction error: $error")
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
        // Judul
        Text(
            text = "Hasil Prediksi",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Menampilkan Nominal
        Text(
            text = "Nominal = $currentNominal ribu",
            fontSize = 24.sp,
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Menampilkan Confidence
        Text(
            text = "dengan confidence ${"%.2f".format(currentConfidence * 100)} persen",
            fontSize = 20.sp,
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Tombol "Kembali ke Home"
        Button(
            onClick = {
                // Navigasi kembali ke HomeScreen tanpa membuka kamera
                navController.navigate(Destinations.HomeScreen.route) {
                    popUpTo("home_screen") { inclusive = true }
                }
            },
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
        ) {
            Text(text = "Kembali ke Home")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Tombol "Scan Ulang"
        Button(
            onClick = {
                // Cek izin kamera
                when {
                    ContextCompat.checkSelfPermission(
                        context,
                        Manifest.permission.CAMERA
                    ) == PackageManager.PERMISSION_GRANTED -> {
                        // Izin kamera sudah diberikan, buka kamera
                        cameraLauncher.launch(Intent(MediaStore.ACTION_IMAGE_CAPTURE))
                    }
                    else -> {
                        // Meminta izin kamera
                        permissionLauncher.launch(Manifest.permission.CAMERA)
                    }
                }
            },
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
        ) {
            Text(text = "Scan Ulang")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Tampilkan pesan error jika ada
        errorMessage?.let {
            Text(text = "Error: $it", color = MaterialTheme.colorScheme.error)
        }
    }
}

/**
 * Fungsi untuk memetakan kelas ke nominal.
 * @param classIndex Indeks kelas sebagai String.
 * @return Nominal yang sesuai dengan kelas tersebut.
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
 * Fungsi untuk mengonversi Bitmap ke ByteBuffer yang sesuai dengan input model.
 * @param bitmap Gambar yang akan diproses.
 * @return ByteBuffer hasil konversi.
 */
fun convertBitmapToByteBuffer(bitmap: Bitmap): ByteBuffer {
    val inputSize = 224
    val byteBuffer = ByteBuffer.allocateDirect(4 * inputSize * inputSize * 3)
    byteBuffer.order(ByteOrder.nativeOrder())

    val scaledBitmap = Bitmap.createScaledBitmap(bitmap, inputSize, inputSize, false)
    for (i in 0 until inputSize) {
        for (j in 0 until inputSize) {
            val pixel = scaledBitmap.getPixel(i, j)
            byteBuffer.putFloat(((pixel shr 16) and 0xFF) / 255.0f)  // R
            byteBuffer.putFloat(((pixel shr 8) and 0xFF) / 255.0f)   // G
            byteBuffer.putFloat((pixel and 0xFF) / 255.0f)           // B
        }
    }

    return byteBuffer
}