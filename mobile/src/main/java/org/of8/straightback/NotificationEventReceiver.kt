package org.of8.straightback

import android.content.Context
import android.content.Intent
import android.app.AlarmManager
import android.app.PendingIntent
import androidx.legacy.content.WakefulBroadcastReceiver
import java.util.*

class NotificationEventReceiver: WakefulBroadcastReceiver() {

    private val ACTION_START_NOTIFICATION_SERVICE = "ACTION_START_NOTIFICATION_SERVICE"
    private val ACTION_DELETE_NOTIFICATION = "ACTION_DELETE_NOTIFICATION"
    private val NOTIFICATIONS_INTERVAL_IN_HOURS = 1

    override fun onReceive(context: Context?, intent: Intent?) {
        val action = intent?.action
        var serviceIntent: Intent? = null
        if (ACTION_START_NOTIFICATION_SERVICE == action) {
            serviceIntent = NotificationIntentService().createIntentStartNotificationService(context)
        } else if (ACTION_DELETE_NOTIFICATION == action) {
            serviceIntent = NotificationIntentService().createIntentDeleteNotification(context)
        }

        if (serviceIntent != null) {
            startWakefulService(context, serviceIntent)
        }
    }

    fun getDeleteIntent(context: Context): PendingIntent {
        val intent = Intent(context, NotificationEventReceiver::class.java)
        intent.action = ACTION_DELETE_NOTIFICATION
        return PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
    }

    fun setupAlarm(context: Context?) {
        if (context == null) {
            return
        }
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val alarmIntent = getStartPendingIntent(context)
        alarmManager.setRepeating(
                AlarmManager.RTC_WAKEUP,
                getTriggerAt(Date()),
                NOTIFICATIONS_INTERVAL_IN_HOURS * AlarmManager.INTERVAL_HOUR,
                alarmIntent)
    }

    fun cancelAlarm(context: Context?) {
        if (context == null) {
            return
        }
        val alarmIntent = getStartPendingIntent(context)
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.cancel(alarmIntent)
        alarmIntent.cancel()
    }

    fun isAlarmRunning(context: Context): Boolean {
        val intent = notificationReceiverIntent(context)
        return PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_NO_CREATE) != null
    }

    private fun getTriggerAt(now: Date): Long {
        val calendar = Calendar.getInstance()
        calendar.time = now
        return calendar.timeInMillis
    }

    private fun getStartPendingIntent(context: Context): PendingIntent {
        val intent = notificationReceiverIntent(context)
        return PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
    }

    private fun notificationReceiverIntent(context: Context): Intent {
        val intent = Intent(context, NotificationEventReceiver::class.java)
        intent.action = ACTION_START_NOTIFICATION_SERVICE
        return  intent
    }

}
