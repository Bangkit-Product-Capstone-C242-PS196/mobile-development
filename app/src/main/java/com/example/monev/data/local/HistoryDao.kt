package com.example.monev.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.monev.data.model.History
import kotlinx.coroutines.flow.Flow

@Dao
interface HistoryDao {
    @Query("SELECT * FROM histories WHERE userId = :userId ORDER BY date DESC")
    fun getAllHistories(userId: String): Flow<List<History>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHistory(history: History)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHistories(histories: List<History>)

    @Delete
    suspend fun deleteHistory(history: History)

    @Query("DELETE FROM histories WHERE userId = :userId")
    suspend fun deleteAllHistories(userId: String)
}
