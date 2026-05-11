package com.nammahasiru.app.worker

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.nammahasiru.app.MainActivity
import com.nammahasiru.app.R
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class PlantReminderWorker(
    context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    companion object {
        const val CHANNEL_ID = "plant_reminder_channel"
        const val CHANNEL_NAME = "Plant Check-up Reminders"
        const val NOTIFICATION_ID_BASE = 1000
    }

    override suspend fun doWork(): Result {
        val plantId = inputData.getInt("plant_id", -1)
        val speciesName = inputData.getString("species_name") ?: "Your tree"
        val datePlanted = inputData.getLong("date_planted", 0L)

        if (plantId == -1) return Result.failure()

        val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
        val dateString = dateFormat.format(Date(datePlanted))

        createNotificationChannel()
        showNotification(plantId, speciesName, dateString)

        return Result.success()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Reminders to check on your planted trees"
            }
            val notificationManager =
                applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun showNotification(plantId: Int, speciesName: String, datePlanted: String) {
        val intent = Intent(applicationContext, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("navigate_to_plant", plantId)
        }

        val pendingIntent = PendingIntent.getActivity(
            applicationContext,
            plantId,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle("Plant Check-up Reminder")
            .setContentText("Please update the growth status of your planted sapling.")
            .setStyle(NotificationCompat.BigTextStyle()
                .bigText("$speciesName planted on $datePlanted is due for a check-up. " +
                         "Please update the growth status of your planted sapling."))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        val notificationManager =
            applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(NOTIFICATION_ID_BASE + plantId, notification)
    }
}
