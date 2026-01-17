package com.example.quest.util

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import com.example.quest.domain.model.Task
import com.example.quest.receiver.NotificationReceiver
import java.time.ZoneId

object NotificationScheduler {

    @SuppressLint("ScheduleExactAlarm")
    fun schedule(context: Context, task: Task) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, NotificationReceiver::class.java).apply {
            putExtra("TASK_ID", task.id)
            putExtra("TASK_TITLE", task.title)
        }
        
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            task.id.toInt(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val dueDate = task.dueDate ?: return
        val timeInMillis = dueDate.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()

        if (timeInMillis <= System.currentTimeMillis()) return

        try {
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                timeInMillis,
                pendingIntent
            )
            Log.d("NotificationScheduler", "Scheduled alarm for task ${task.id} at $dueDate")
        } catch (e: SecurityException) {
            Log.e("NotificationScheduler", "Permission denied for exact alarm", e)
        }
    }

    fun cancel(context: Context, task: Task) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, NotificationReceiver::class.java)
        
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            task.id.toInt(),
            intent,
            PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
        )

        if (pendingIntent != null) {
            alarmManager.cancel(pendingIntent)
            Log.d("NotificationScheduler", "Cancelled alarm for task ${task.id}")
        }
    }
}
