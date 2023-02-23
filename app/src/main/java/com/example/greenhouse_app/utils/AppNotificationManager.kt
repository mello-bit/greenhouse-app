package com.example.greenhouse_app.utils

import android.annotation.SuppressLint
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.greenhouse_app.MainActivity
import com.example.greenhouse_app.R

class AppNotificationManager {

    companion object {
        const val CHANNEL_ID = "greenhouse temp notification"
        @SuppressLint("StaticFieldLeak")
        private lateinit var context: Context

        private lateinit var notificationManager: NotificationManager


        fun initContext(application_context: Context) {
            context = application_context
            notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            Log.d("TempTag", "Initialized AppNotification")
        }

        fun showNotification() {
            val activityIntent = Intent(context, MainActivity::class.java)
            val activityPendingIntent = PendingIntent.getActivity(
                context,
                0,
                activityIntent,
                PendingIntent.FLAG_IMMUTABLE
            )
            val notification = NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.mipmap.notification_icon_foreground)
                .setContentTitle("Ваша теплица горит!!!")
                .setContentText("У вас прекрасная теплица была!!!")
                .setContentInfo("блалааалалалалалалалалалвоираиамианг")
                .setContentIntent(activityPendingIntent)
                .build()

            notificationManager.notify(1, notification)

        }

    }

}