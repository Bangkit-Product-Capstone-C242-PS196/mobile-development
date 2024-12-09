package com.example.monev.utils

import android.content.Context
import android.content.SharedPreferences

class PreferenceManager(context: Context) {

    companion object {
        private const val PREF_NAME = "user_preferences"
        private const val KEY_NOTIFICATION_STATUS = "notification_status"
    }

    private val preferences: SharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

    // Simpan status notifikasi (true / false)
    fun setNotificationStatus(isEnabled: Boolean) {
        preferences.edit().putBoolean(KEY_NOTIFICATION_STATUS, isEnabled).apply()
    }

    // Ambil status notifikasi (default = false)
    fun getNotificationStatus(): Boolean {
        return preferences.getBoolean(KEY_NOTIFICATION_STATUS, false)
    }

    // Hapus data (opsional jika logout)
    fun clearPreferences() {
        preferences.edit().clear().apply()
    }
}
