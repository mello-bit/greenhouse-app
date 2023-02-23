package com.example.greenhouse_app

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.util.Log
import com.example.greenhouse_app.utils.AppSettingsManager
import com.example.greenhouse_app.utils.AppNotificationManager

class MyApplication : Application() {
    override fun onCreate() {
        Log.d("TempTag", "Started application")
        AppSettingsManager.initContext(applicationContext)
        AppNotificationManager.initContext(applicationContext)
        Log.d("TempTag", "${AppSettingsManager.checkThatDataExists("tempValue")}")
        super.onCreate()
        createNotificationChannel()
        // Initialize the Greenhouse instance
//        Greenhouse.initialize(this)
//        val ap = AppSettingsManager()
//        ap.initContext(con)
    }

    override fun onTerminate() {
//        Greenhouse.getInstance().saveState()
        super.onTerminate()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                AppNotificationManager.CHANNEL_ID,
                "greenhouse notification",
                NotificationManager.IMPORTANCE_HIGH
            )

            channel.description = "Some description of my notification"

            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
}