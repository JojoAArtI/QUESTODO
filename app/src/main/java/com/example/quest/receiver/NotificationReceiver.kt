package com.example.quest.receiver

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.example.quest.MainActivity
import com.example.quest.R
import android.util.Log

class NotificationReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val taskId = intent.getLongExtra("TASK_ID", -1)
        val taskTitle = intent.getStringExtra("TASK_TITLE") ?: "Task"
        
        if (taskId == -1L) return

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        
        // Intent to open app when clicked
        val contentIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            context, 
            taskId.toInt(), 
            contentIntent, 
            PendingIntent.FLAG_IMMUTABLE
        )

        // Humane/Witty messages
        val messages = listOf(
            "Time for '$taskTitle'. Chop chop.",
            "'$taskTitle' is due. Don't disappoint your ancestors.",
            "Hey, remember '$taskTitle'? Me neither, but the code says do it.",
            "Procrastination alert: '$taskTitle' needs you.",
            "Do '$taskTitle' or else... (nothing happens, but still)."
        )
        val message = messages.random()

        val notification = NotificationCompat.Builder(context, "task_channel")
            .setSmallIcon(R.mipmap.ic_launcher_round) // Fallback icon
            .setContentTitle("Task Reminder")
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()
            
        try {
            notificationManager.notify(taskId.toInt(), notification)
            Log.d("NotificationReceiver", "Notification sent for $taskId")
        } catch (e: Exception) {
            Log.e("NotificationReceiver", "Failed to send notification", e)
        }
    }
}
