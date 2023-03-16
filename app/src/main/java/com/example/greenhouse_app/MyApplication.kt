package com.example.greenhouse_app

import android.annotation.SuppressLint
import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.room.Room
import com.example.greenhouse_app.dataClasses.AllData
import com.example.greenhouse_app.dataClasses.ListForData
import com.example.greenhouse_app.recyclerView.DataAdapter
import com.example.greenhouse_app.fragments.ApiListener
import com.example.greenhouse_app.utils.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.math.round
import kotlin.math.roundToInt

fun getCurrentDateTimeISO8601(): String {
    val formatter = DateTimeFormatter.ISO_DATE_TIME
    return Instant.now().toString()
}

class MyApplication : Application() {
    val DEBUGMODE = false
    var loggedIn = DEBUGMODE

    private lateinit var networkManager: AppNetworkManager
    private lateinit var handler: Handler
    private lateinit var db: AppDatabase
    private lateinit var sensorDao: SensorDao
    private var decimalFormatter = DecimalFormat("#.##", DecimalFormatSymbols(Locale.US))
    private var apiListener: ApiListener? = null
    val myAdapter by lazy { DataAdapter() }
    var userEmail: String = "test@mail.ru"
    var currentUID: String = "UNASSIGNED"
        set(uid) {
            field = uid
            loggedIn = true

            db = AppDatabaseHelper.getDatabase(applicationContext, uid)
            sensorDao = db.SensorDao()

            networkManager = AppNetworkManager(applicationContext)
            handler = Handler(Looper.getMainLooper())

            handler.post(object : Runnable {
                override fun run() {

                    networkManager.getSoilHum()
                    networkManager.getTempAndHum()
                    handler.postDelayed(this, 10 * 1000)

                    if (ListForData.SoilHumList.size == 6 && ListForData.TempAndHumList.size == 4) {
                        ListForData.EverySoilHumDataList.add(
                            toAllSoilHumDataClass()
                        )
//                        Log.d("CheckTag", ListForData.EverySoilHumDataList.toString())
                        myAdapter.setData(ListForData.EverySoilHumDataList)
                        myAdapter.notifyItemInserted(ListForData.EverySoilHumDataList.size - 1)

                        apiListener?.onApiResponseReceived(Pair(ListForData.SoilHumList, ListForData.TempAndHumList))
                        saveEntryToDB(ListForData)

                        ListForData.SoilHumList.clear()
                        ListForData.TempAndHumList.clear()

                    }

                }
            })
        }

    fun setApiListener(listener: ApiListener) {
        this.apiListener = listener
    }


    private fun saveEntryToDB(data: ListForData.Companion) {
        var totalGreenhouseTemperature = 0f
        var totalGreenhouseHumidity = 0f
        for (entry in data.TempAndHumList) {
            totalGreenhouseTemperature += entry.temp.toFloat()
            totalGreenhouseHumidity += entry.hum.toFloat()
        }

        val avgTemp = decimalFormatter.format(totalGreenhouseTemperature / data.TempAndHumList.size.toFloat())
        val avgHumidity = decimalFormatter.format(totalGreenhouseHumidity / data.TempAndHumList.size.toFloat())

        val soilHumListCopy = data.SoilHumList.toList()

        CoroutineScope(Dispatchers.IO).launch {
            val dataToSave = SensorData(
                createdAt = getCurrentDateTimeISO8601(),
                greenhouse_temperature = avgTemp.toFloat(),
                greenhouse_humidity = avgHumidity.toFloat(),
                furrow1_humidity = soilHumListCopy[0].humidity.toFloat(),
                furrow2_humidity = soilHumListCopy[1].humidity.toFloat(),
                furrow3_humidity = soilHumListCopy[2].humidity.toFloat(),
                furrow4_humidity = soilHumListCopy[3].humidity.toFloat(),
                furrow5_humidity = soilHumListCopy[4].humidity.toFloat(),
                furrow6_humidity = soilHumListCopy[5].humidity.toFloat()
            )

            sensorDao.insertData(dataToSave)
        }
    }

    override fun onCreate() {
        super.onCreate()

        AppSettingsManager.initContext(applicationContext)
        AppNotificationManager.initContext(applicationContext)

        if (AppSettingsManager.checkThatDataExists("WereSettingsLoadedOnce")) {
            AppSettingsManager.isAllBoundaryDataExists = true
        } else {
            AppSettingsManager.saveData("Language", "RU")
            AppSettingsManager.saveData("TempUnits", "C")
            AppSettingsManager.saveData("Interval", "60")
            AppSettingsManager.saveData("EmergencyMode", "false")
            AppSettingsManager.saveData("WereSettingsLoadedOnce", "true")
        }

        if (DEBUGMODE && currentUID == "UNASSIGNED") {
            currentUID = "DEBUG"
        }
        createNotificationChannel()
    }

    private fun toAllSoilHumDataClass(): AllData{
        ListForData.SoilHumList.sortBy {
            it.id
        }
        ListForData.TempAndHumList.sortBy {
            it.id
        }
        val formatter = DateTimeFormatter.ofPattern("dd.mm.yyyy HH:mm:ss")
        val current = LocalDateTime.now().format(formatter)

        return AllData(
            current,
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