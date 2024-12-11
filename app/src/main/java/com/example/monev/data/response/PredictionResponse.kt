package com.example.monev.data.response

import com.google.gson.annotations.SerializedName


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