package com.example.monev.data.repository

import android.util.Log
import com.example.monev.data.local.HistoryDao
import com.example.monev.data.model.History
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await


class HistoryRepository private constructor(val historyDao: HistoryDao) {
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    private var firestoreListener: ListenerRegistration? = null
    private val scope = CoroutineScope(IO)

    companion object {
        @Volatile
        private var INSTANCE: HistoryRepository? = null

        fun getInstance(historyDao: HistoryDao): HistoryRepository {
            return INSTANCE ?: synchronized(this) {
                val instance = HistoryRepository(historyDao)
                INSTANCE = instance
                instance
            }
        }
    }

    // Mendapatkan semua history dari Room
    fun getAllHistories(): Flow<List<History>> {
        val currentUser = auth.currentUser
        if (currentUser != null && firestoreListener == null) {
            // Menambahkan listener Firestore hanya sekali
            firestoreListener = db.collection("users")
                .document(currentUser.uid)
                .collection("histories")
                .addSnapshotListener { snapshots, e ->
                    if (e != null) {
                        Log.w("FirestoreListener", "Listen failed.", e)
                        return@addSnapshotListener
                    }

                    if (snapshots != null) {
                        scope.launch {
                            try {
                                val histories = snapshots.documents.mapNotNull { doc ->
                                    doc.toObject(History::class.java)?.copy(
                                        firestoreId = doc.id,
                                        userId = currentUser.uid
                                    )
                                }
                                Log.d("FirestoreListener", "Histories fetched from Firestore: $histories")

                                // Sinkronisasi dengan Room
                                historyDao.deleteAllHistories(currentUser.uid)
                                Log.d("FirestoreListener", "Deleted all histories from Room for user: ${currentUser.uid}")
                                historyDao.insertHistories(histories)
                                Log.d("FirestoreListener", "Inserted histories into Room: $histories")
                            } catch (ex: Exception) {
                                Log.e("FirestoreListener", "Error during synchronization: ${ex.message}")
                            }
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

    /**
     * Menambahkan history ke Firestore saja.
     * @param history Objek History yang akan ditambahkan.
     */
    suspend fun addHistory(history: History) {
        val currentUser = auth.currentUser
        if (currentUser != null) {
            try {
                // Pastikan date adalah timestamp dalam format String
                val timestamp = history.date.toLongOrNull() ?: System.currentTimeMillis()

                // Tambahkan ke Firestore dengan date disimpan sebagai Long
                db.collection("users")
                    .document(currentUser.uid)
                    .collection("histories")
                    .add(history.copy(userId = currentUser.uid, date = timestamp.toString()))
                    .await()

                Log.d("HistoryRepository", "History added to Firestore: $history")
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
                Log.d("HistoryRepository", "Deleted all histories from Room for user: ${currentUser.uid}")
                historyDao.insertHistories(histories)
                Log.d("HistoryRepository", "Inserted histories into Room from sync: $histories")
            } catch (e: Exception) {
                Log.e("HistoryRepository", "Error syncing histories: ${e.message}")
            }
        }
    }

    // Menghapus listener Firestore
    fun removeListener() {
        firestoreListener?.remove()
        Log.d("HistoryRepository", "Firestore listener removed")
    }

    // Mendapatkan user saat ini
    fun getCurrentUser() = auth.currentUser
}
