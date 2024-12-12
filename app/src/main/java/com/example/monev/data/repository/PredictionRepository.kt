package com.example.monev.data.repository

import android.graphics.Bitmap
import android.util.Log
import com.example.monev.data.response.PredictionResponse
import com.example.monev.data.retrofit.PredictionApi
import com.example.monev.data.retrofit.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.ByteArrayOutputStream

class PredictionRepository(private val api: PredictionApi = RetrofitClient.instance) {

    suspend fun predictImage(bitmap: Bitmap): Result<PredictionResponse> {
        return withContext(Dispatchers.IO) {
            try {
                val byteArray = bitmapToByteArray(bitmap)
                val requestFile = RequestBody.create("image/jpeg".toMediaTypeOrNull(), byteArray)
                val body = MultipartBody.Part.createFormData("image", "image.jpg", requestFile)

                val response = api.predictImage(body)
                if (response.isSuccessful) {
                    response.body()?.let {
                        Result.success(it)
                    } ?: Result.failure(Exception("Response body is null"))
                } else {
                    Result.failure(Exception("Response not successful: ${response.message()}"))
                }
            } catch (e: Exception) {
                Log.e("PredictionRepository", "Error in predictImage: ${e.message}")
                Result.failure(e)
            }
        }
    }

    private fun bitmapToByteArray(bitmap: Bitmap): ByteArray {
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
        return stream.toByteArray()
    }
}