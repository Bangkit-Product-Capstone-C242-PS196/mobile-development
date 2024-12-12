package com.example.monev.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "histories")
data class History(
    @PrimaryKey(autoGenerate = true) val localId: Int = 0,
    val firestoreId: String = "", // ID dari Firestore
    val userId: String = "", // ID pengguna
    val nominal: String = "",
    val date: String = "",
    val confidence: Float = 0f,
    val photo: String = ""
)