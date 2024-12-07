package com.example.monev.data.repository

import android.util.Log
import com.example.monev.data.local.HistoryDao
import com.example.monev.data.model.History
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class HistoryRepository(private val historyDao: HistoryDao) {
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    private var firestoreListener: ListenerRegistration? = null
    private val scope = CoroutineScope(Dispatchers.IO)

    // Mendapatkan semua history dari Room
    fun getAllHistories(): Flow<List<History>> {
        val currentUser = auth.currentUser
        if (currentUser != null) {
            // Menambahkan listener Firestore
            firestoreListener = db.collection("users")
                .document(currentUser.uid)
                .collection("histories")
                .addSnapshotListener { snapshots, e ->
                    if (e != null) {
                        Log.w("Firestore", "Listen failed.", e)
                        return@addSnapshotListener
                    }

                    if (snapshots != null) {
                        scope.launch {
                            val histories = snapshots.documents.mapNotNull { doc ->
                                doc.toObject(History::class.java)?.copy(firestoreId = doc.id, userId = currentUser.uid)
                            }
                            // Sinkronisasi dengan Room
                            historyDao.deleteAllHistories(currentUser.uid)
                            historyDao.insertHistories(histories)
                        }
                    }
                }
        }

        return if (currentUser != null) {
            historyDao.getAllHistories(currentUser.uid)
        } else {
            emptyFlow()
        }
    }

    suspend fun addHistory(history: History) {
        val currentUser = auth.currentUser
        if (currentUser != null) {
            try {
                // Pastikan date adalah timestamp dalam format String
                val timestamp = history.date.toLongOrNull() ?: System.currentTimeMillis()

                // Tambahkan ke Firestore dengan date disimpan sebagai Long
                val docRef = db.collection("users")
                    .document(currentUser.uid)
                    .collection("histories")
                    .add(history.copy(userId = currentUser.uid, date = timestamp.toString()))
                    .await()

                // Tambahkan ke Room dengan firestoreId
                historyDao.insertHistory(history.copy(userId = currentUser.uid, firestoreId = docRef.id))
            } catch (e: Exception) {
                Log.e("HistoryRepository", "Error adding history: ${e.message}")
            }
        }
    }



    suspend fun syncHistoriesFromFirestore() {
        val currentUser = auth.currentUser
        if (currentUser != null) {
            try {
                val snapshot = db.collection("users")
                    .document(currentUser.uid)
                    .collection("histories")
                    .get()
                    .await()

                val histories = snapshot.documents.mapNotNull { doc ->
                    // Mengambil date dan mengonversinya ke Long jika perlu
                    val dateAsString = doc.getString("date") ?: System.currentTimeMillis().toString()

                    // Membuat objek History dengan date sebagai String
                    doc.toObject(History::class.java)?.copy(
                        firestoreId = doc.id,
                        userId = currentUser.uid,
                        date = dateAsString // Simpan date sebagai String
                    )
                }

                // Sinkronisasi dengan Room
                historyDao.deleteAllHistories(currentUser.uid)
                historyDao.insertHistories(histories)
            } catch (e: Exception) {
                Log.e("HistoryRepository", "Error syncing histories: ${e.message}")
            }
        }
    }



    // Menghapus listener Firestore
    fun removeListener() {
        firestoreListener?.remove()
    }
}
