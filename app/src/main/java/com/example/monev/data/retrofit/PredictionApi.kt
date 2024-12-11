package com.example.monev.data.retrofit

import com.example.monev.data.response.PredictionResponse
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
