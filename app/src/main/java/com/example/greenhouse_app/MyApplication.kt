package com.example.greenhouse_app

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.util.Log
import com.example.greenhouse_app.dataClasses.ListForData
import com.example.greenhouse_app.fragments.ApiListener
import com.example.greenhouse_app.utils.AppNetworkManager
import com.example.greenhouse_app.utils.AppSettingsManager
import com.example.greenhouse_app.utils.AppNotificationManager

class MyApplication : Application() {

    private lateinit var networkManager: AppNetworkManager
    private lateinit var handler: Handler
    private var apiListener: ApiListener? = null

    fun setApiListener(listener: ApiListener) {
        this.apiListener = listener
    }

    override fun onCreate() {
        Log.d("TempTag", "Started application")

        AppSettingsManager.initContext(applicationContext)
        AppNotificationManager.initContext(applicationContext)

        if (AppSettingsManager.checkThatDataExists("tempValue") &&
                AppSettingsManager.checkThatDataExists("humValue")&&
                AppSettingsManager.checkThatDataExists("soilValue")) {
            AppSettingsManager.isAllBoundaryDataExists = true
        }

        super.onCreate()
        createNotificationChannel()
        networkManager = AppNetworkManager(applicationContext)
        handler = Handler(Looper.getMainLooper())
        // Initialize the Greenhouse instance
//        Greenhouse.initialize(this)
//        val ap = AppSettingsManager()
//        ap.initContext(con)

//        networkManager.getSoilHum()
//        networkManager.getTempAndHum()
        handler.post(object : Runnable {
            override fun run() {
                networkManager.getSoilHum()
                networkManager.getTempAndHum()
//                handler.postDelayed(this, 200)
                handler.postDelayed(this, 10 * 1000)
                if (ListForData.SoilHumList.size == 6 && ListForData.TempAndHumList.size == 4) {
                    apiListener?.onApiResponseReceived(Pair(ListForData.SoilHumList, ListForData.TempAndHumList))

                    Log.d("MyLog",  "Множество почва ${ListForData.SoilHumList.toString()}")
                    Log.d("MyLog", "Множество сенсоры ${ListForData.TempAndHumList.toString()}")
                    ListForData.SoilHumList.clear()
                    ListForData.TempAndHumList.clear()
                }

            }
        })
    }

    private fun callSoilHumApi() {

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