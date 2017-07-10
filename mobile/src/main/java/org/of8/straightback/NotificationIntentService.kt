package org.of8.straightback

import android.app.IntentService
import android.content.Intent
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.graphics.BitmapFactory
import android.os.Build
import android.support.v4.content.WakefulBroadcastReceiver
import android.support.v7.app.NotificationCompat
import android.support.v4.app.NotificationCompat.WearableExtender;

import android.util.Log
import android.media.RingtoneManager



class NotificationIntentService(name: String?) : IntentService(name) {

    private val NOTIFICATION_ID = 1
    private val ACTION_START = "ACTION_START"
    private val ACTION_DELETE = "ACTION_DELETE"

    constructor() : this(NotificationIntentService::class.java.simpleName)

    override fun onHandleIntent(intent: Intent?) {
        try {
            val action = intent?.action
            if (ACTION_START == action) {
                processStartNotification()
            }
            if (ACTION_DELETE == action) {
                processDeleteNotification()
            }
        } finally {
            WakefulBroadcastReceiver.completeWakefulIntent(intent)
        }
    }

    fun createIntentStartNotificationService(context: Context?): Intent {
        val intent = Intent(context, NotificationIntentService::class.java)
        intent.action = ACTION_START
        return intent
    }

    fun createIntentDeleteNotification(context: Context?): Intent {
        val intent = Intent(context, NotificationIntentService::class.java)
        intent.action = ACTION_DELETE
        return intent
    }

    private fun processStartNotification() {
        val wearBackground = BitmapFactory.decodeResource(resources, R.drawable.wear_background)
        val wearableExtender = WearableExtender().setBackground(wearBackground)
        val builder = NotificationCompat.Builder(this)
        builder.setContentTitle(getString(R.string.notification_title))
                .setAutoCancel(true)
                .setOngoing(false)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setContentText(getString(R.string.notification_description))
                .setSmallIcon(R.drawable.ic_airline_seat_recline_normal_white_24dp)
                .setOnlyAlertOnce(false)
                .setVibrate(longArrayOf(1000, 1000))
                .extend(wearableExtender)

        if (Build.VERSION.SDK_INT < 21) {
            val largeIcon = BitmapFactory.decodeResource(resources, R.drawable.ic_airline_seat_recline_normal_white_48dp)
            builder.setLargeIcon(largeIcon)
        }

        val pendingIntent = PendingIntent.getActivity(this,
                NOTIFICATION_ID,
                Intent(this, MainActivity::class.java),
                PendingIntent.FLAG_UPDATE_CURRENT)
        builder.setContentIntent(pendingIntent)
        builder.setDeleteIntent(NotificationEventReceiver().getDeleteIntent(this))

        val manager = this.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(NOTIFICATION_ID, builder.build())
    }

    private fun processDeleteNotification() {
    }

}
