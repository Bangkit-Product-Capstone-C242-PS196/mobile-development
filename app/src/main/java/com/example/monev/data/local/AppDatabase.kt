package com.example.monev.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.monev.data.model.History


@Database(entities = [History::class], version = 2, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun historyDao(): HistoryDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "auth_database"
                )
                    .addMigrations(MIGRATION_1_2)
                    .fallbackToDestructiveMigration() // Tambahkan ini untuk pengujian
                    .build()
                INSTANCE = instance
                instance
            }
        }

        // Definisikan migrasi dari versi 1 ke 2
        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // Tambah kolom 'confidence' dengan nilai default 0
                database.execSQL("ALTER TABLE histories ADD COLUMN confidence REAL NOT NULL DEFAULT 0")
                // Tambah indeks unik pada 'firestoreId'
                database.execSQL("CREATE UNIQUE INDEX index_histories_firestoreId ON histories(firestoreId)")
            }
        }
    }
}
