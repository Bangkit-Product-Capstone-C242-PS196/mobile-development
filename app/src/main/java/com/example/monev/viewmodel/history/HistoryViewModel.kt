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
        repository = HistoryRepository(historyDao)
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

    fun addHistory(history: History) {
        viewModelScope.launch {
            try {
                repository.addHistory(history)
            } catch (e: Exception) {
                Log.e("HistoryViewModel", "Error adding history: ${e.message}")
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        repository.removeListener()
    }
}
