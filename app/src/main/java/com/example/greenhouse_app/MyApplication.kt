package com.example.greenhouse_app

import android.annotation.SuppressLint
import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.os.Handler
import android.os.Looper
import com.example.greenhouse_app.dataClasses.AllData
import com.example.greenhouse_app.dataClasses.ListForData
import com.example.greenhouse_app.recyclerView.DataAdapter
import com.example.greenhouse_app.utils.AppNetworkManager
import com.example.greenhouse_app.utils.AppSettingsManager
import com.example.greenhouse_app.utils.AppNotificationManager

class MyApplication : Application() {

    private lateinit var networkManager: AppNetworkManager
    private lateinit var handler: Handler
    val myAdapter by lazy { DataAdapter() }

    override fun onCreate() {
        AppSettingsManager.initContext(applicationContext)
        AppNotificationManager.initContext(applicationContext)

        if (AppSettingsManager.checkThatDataExists("tempValue") &&
                AppSettingsManager.checkThatDataExists("humValue")&&
                AppSettingsManager.checkThatDataExists("soilValue")) {
            AppSettingsManager.isAllBoundaryDataExists = true
        } else {
            AppSettingsManager.saveData("tempValue", "21")
            AppSettingsManager.saveData("humValue", "69.7")
            AppSettingsManager.saveData("soilValue", "67.4")
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
            @SuppressLint("NotifyDataSetChanged")
            override fun run() {
                networkManager.getSoilHum()
                networkManager.getTempAndHum()
                handler.postDelayed(this, 10 * 1000)
                if (ListForData.SoilHumList.size == 6 && ListForData.TempAndHumList.size == 4) {
                    ListForData.EverySoilHumDataList.add(
                        toAllSoilHumDataClass()
                    )

                    myAdapter.setData(ListForData.EverySoilHumDataList)
                    myAdapter.notifyDataSetChanged()

                    ListForData.SoilHumList.clear()
                    ListForData.TempAndHumList.clear()

                }

            }
        })
    }

    private fun toAllSoilHumDataClass(): AllData{
        ListForData.SoilHumList.sortBy {
            it.id
        }
        ListForData.TempAndHumList.sortBy {
            it.id
        }
        return AllData(
            ListForData.SoilHumList[0].humidity,
            ListForData.SoilHumList[1].humidity,
            ListForData.SoilHumList[2].humidity,
            ListForData.SoilHumList[3].humidity,
            ListForData.SoilHumList[4].humidity,
            ListForData.SoilHumList[5].humidity,
            ListForData.TempAndHumList[0].temp,
            ListForData.TempAndHumList[1].temp,
            ListForData.TempAndHumList[2].temp,
            ListForData.TempAndHumList[3].temp,
            ListForData.TempAndHumList[0].hum,
            ListForData.TempAndHumList[1].hum,
            ListForData.TempAndHumList[2].hum,
            ListForData.TempAndHumList[3].hum,
        )
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