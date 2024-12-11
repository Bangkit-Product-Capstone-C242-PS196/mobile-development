package com.example.monev.viewmodel.history

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.monev.data.local.AppDatabase
import com.example.monev.data.model.History
import com.example.monev.data.repository.HistoryRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class HistoryViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: HistoryRepository

    // StateFlow untuk menampung list of History dari Room
    val histories: StateFlow<List<History>>

    init {
        val historyDao = AppDatabase.getDatabase(application).historyDao()
        repository = HistoryRepository.getInstance(historyDao) // Menggunakan singleton
        // Mengambil aliran data dan mengkonversinya menjadi StateFlow
        histories = repository.getAllHistories()
            .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

        // Sinkronisasi awal dari Firestore ke Room
        viewModelScope.launch {
            try {
                repository.syncHistoriesFromFirestore()
            } catch (e: Exception) {
                Log.e("HistoryViewModel", "Error syncing data from Firestore: ${e.message}")
            }
        }
    }

    /**
     * Menambahkan history baru dengan nominal, confidence, dan photo.
     * @param nominal Nilai nominal yang diprediksi (misalnya, "1k", "2k", dll.)
     * @param confidence Tingkat kepercayaan prediksi
     * @param photo URL foto terkait history. Secara default diisi dengan "default_photo_url"
     */
    fun addHistory(nominal: String, confidence: Float, photo: String = "default_photo_url") {
        viewModelScope.launch {
            try {
                val currentUser = repository.getCurrentUser()
                if (currentUser != null) {
                    val date = System.currentTimeMillis().toString()
                    val history = History(
                        userId = currentUser.uid,
                        nominal = nominal,
                        confidence = confidence,
                        date = date,
                        photo = photo
                    )
                    repository.addHistory(history)
                    Log.d("HistoryViewModel", "History added: $history")
                } else {
                    Log.e("HistoryViewModel", "No current user found.")
                }
            } catch (e: Exception) {
                Log.e("HistoryViewModel", "Error adding history: ${e.message}")
            }
        }
    }

    /**
     * Menambahkan history dengan objek History secara langsung.
     * @param history Objek History yang akan ditambahkan
     */
    fun addHistoryDirectly(nominal: String, confidence: Float, photo: String = "default_photo_url") {
        viewModelScope.launch {
            try {
                val currentUser = repository.getCurrentUser()
                if (currentUser != null) {
                    val date = System.currentTimeMillis().toString()
                    val history = History(
                        userId = currentUser.uid,
                        nominal = nominal,
                        confidence = confidence,
                        date = date,
                        photo = photo
                    )
                    // Tambahkan ke Room tanpa Firestore
                    repository.historyDao.insertHistory(history)
                    Log.d("HistoryViewModel", "History langsung ditambahkan ke Room: $history")
                } else {
                    Log.e("HistoryViewModel", "No current user found.")
                }
            } catch (e: Exception) {
                Log.e("HistoryViewModel", "Error menambahkan history langsung ke Room: ${e.message}")
            }
        }
    }

    /**
     * Menghapus semua histories dari Room untuk user saat ini.
     */
    fun deleteAllHistories() {
        viewModelScope.launch {
            try {
                val currentUser = repository.getCurrentUser()
                if (currentUser != null) {
                    repository.historyDao.deleteAllHistories(currentUser.uid)
                    Log.d("HistoryViewModel", "All histories deleted from Room for user: ${currentUser.uid}")
                } else {
                    Log.e("HistoryViewModel", "No current user found.")
                }
            } catch (e: Exception) {
                Log.e("HistoryViewModel", "Error deleting histories: ${e.message}")
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        repository.removeListener()
    }
}
