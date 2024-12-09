package com.example.monev.worker

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.monev.R

const val CHANNEL_ID = "reminder_channel"

fun createNotificationChannel(context: Context) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val name = "Reminder Channel"
        val descriptionText = "Channel for daily reminders"
        val importance = NotificationManager.IMPORTANCE_HIGH
        val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
            description = descriptionText
        }
        val notificationManager: NotificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }
}

fun showDailyNotification(context: Context, title: String, message: String) {
    createNotificationChannel(context)
    val notification = NotificationCompat.Builder(context, CHANNEL_ID)
        .setSmallIcon(R.drawable.ic_launcher_foreground) // Use a valid icon
        .setContentTitle(title)
        .setContentText(message)
        .setPriority(NotificationCompat.PRIORITY_HIGH)
        .build()

    val notificationManager = NotificationManagerCompat.from(context)
    if (notificationManager.areNotificationsEnabled()) {
        notificationManager.notify(1, notification)
    }
}

class DailyReminderWorker(context: Context, workerParams: WorkerParameters) : Worker(context, workerParams) {
    override fun doWork(): Result {
        showDailyNotification(applicationContext, "Daily Reminder", "Don't forget to check your daily history!")
        return Result.success()
    }
}