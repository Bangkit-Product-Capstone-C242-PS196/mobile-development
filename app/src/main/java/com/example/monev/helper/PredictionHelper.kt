package com.example.monev.helper

import android.content.Context
import android.util.Log
import com.google.android.gms.tflite.client.TfLiteInitializationOptions
import com.google.android.gms.tflite.gpu.support.TfLiteGpu
import com.google.android.gms.tflite.java.TfLite
import com.google.firebase.ml.modeldownloader.CustomModel
import com.google.firebase.ml.modeldownloader.CustomModelDownloadConditions
import com.google.firebase.ml.modeldownloader.DownloadType
import com.google.firebase.ml.modeldownloader.FirebaseModelDownloader
import org.tensorflow.lite.InterpreterApi
import org.tensorflow.lite.gpu.GpuDelegateFactory
import java.io.IOException
import java.nio.ByteBuffer

class PredictionHelper(
    private val modelName: String = "modelv1.tflite",
    val context: Context,
    private val onResult: (String, Float) -> Unit,  // Dua parameter: kelas dan confidence
    private val onError: (String) -> Unit,
) {
    private var interpreter: InterpreterApi? = null
    private var isModelReady = false

    init {
        // Inisialisasi TensorFlow Lite
        initializeTensorFlowLite()
    }

    // Fungsi untuk memastikan TensorFlow Lite diinisialisasi
    private fun initializeTensorFlowLite() {
        TfLiteGpu.isGpuDelegateAvailable(context).onSuccessTask { gpuAvailable ->
            val optionsBuilder = TfLiteInitializationOptions.builder()
            if (gpuAvailable) {
                optionsBuilder.setEnableGpuDelegateSupport(true)
            }
            TfLite.initialize(context, optionsBuilder.build())
        }.addOnSuccessListener {
            // Model sudah siap untuk diunduh setelah TfLite diinisialisasi
            downloadModel()
        }.addOnFailureListener { exception ->
            onError("TensorFlow Lite initialization failed: ${exception.message}")
        }
    }

    // Fungsi untuk mendownload model dari Firebase
    @Synchronized
    fun downloadModel() {
        val conditions = CustomModelDownloadConditions.Builder()
            .requireWifi()  // Pastikan koneksi Wi-Fi tersedia
            .build()

        FirebaseModelDownloader.getInstance()
            .getModel("modelv2", DownloadType.LOCAL_MODEL, conditions)
            .addOnSuccessListener { model: CustomModel ->
                try {
                    // Inisialisasi model dan interpreter setelah model berhasil diunduh
                    initializeInterpreter(model)
                    onDownloadSuccess()
                } catch (e: IOException) {
                    onError("Model initialization failed: ${e.message}")
                }
            }
            .addOnFailureListener { e ->
                onError("Model download failed: ${e?.message ?: "Unknown error"}")
            }
    }

    // Fungsi untuk menginisialisasi interpreter
    private fun initializeInterpreter(model: Any) {
        interpreter?.close()  // Tutup interpreter lama jika ada
        try {
            val options = InterpreterApi.Options()
                .setRuntime(InterpreterApi.Options.TfLiteRuntime.FROM_SYSTEM_ONLY)
                .addDelegateFactory(GpuDelegateFactory())

            if (model is ByteBuffer) {
                interpreter = InterpreterApi.create(model, options)
            } else if (model is CustomModel) {
                model.file?.let {
                    interpreter = InterpreterApi.create(it, options)
                }
            }
            isModelReady = true
        } catch (e: Exception) {
            onError("Interpreter initialization failed: ${e.message}")
            Log.e(TAG, e.message.toString())
        }
    }

    // Fungsi prediksi
    fun predict(inputBuffer: ByteBuffer) {
        if (!isModelReady) {
            onError("Model not initialized. Please wait.")
            return
        }

        // Tentukan ukuran output sesuai dengan model Anda
        val outputArray = Array(1) { FloatArray(7) }

        try {
            // Lakukan prediksi
            interpreter?.run(inputBuffer, outputArray)

            // Proses hasil output
            val prediction = outputArray[0]  // Ambil hasil untuk satu batch
            Log.d(TAG, "Full prediction array: ${prediction.joinToString()}")

            val predictedClass = prediction.indexOfFirst { it == prediction.maxOrNull() }
            val confidence = prediction[predictedClass]

            onResult(predictedClass.toString(), confidence)
        } catch (e: Exception) {
            onError("Prediction failed: ${e.message}")
            Log.e(TAG, e.message.toString())
        }
    }

    // Fungsi untuk menginformasikan bahwa model berhasil diunduh
    private fun onDownloadSuccess() {
        Log.d(TAG, "Model downloaded and ready for use.")
    }

    companion object {
        private const val TAG = "PredictionHelper"
    }
}
