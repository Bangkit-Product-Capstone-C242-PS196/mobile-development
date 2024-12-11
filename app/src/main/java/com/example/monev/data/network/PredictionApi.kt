package com.example.monev.data.network

import com.google.gson.annotations.SerializedName
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

// Interface API untuk Retrofit
interface PredictionApi {
    @Multipart
    @POST("predict")
    suspend fun predictImage(
        @Part image: MultipartBody.Part
    ): Response<PredictionResponse>
}

// Data class untuk response dari API
data class PredictionResponse(
    val status: String,
    val message: String,
    val data: Data
)

data class Data(
    val id: String,
    val result: Result
)

data class Result(
    @SerializedName("class") val classId: Int,
    val confidence: Float,
    val score: Int
)