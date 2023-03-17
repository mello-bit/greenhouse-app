package com.example.greenhouse_app.utils

import android.content.Context
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.greenhouse_app.dataClasses.ListForData
import com.example.greenhouse_app.dataClasses.SoilHum
import com.example.greenhouse_app.dataClasses.TempAndHum
import org.json.JSONObject

class AppNetworkManager(private val context: Context?) {

    private val urlForGetSoilHum = "https://dt.miet.ru/ppo_it/api/hum/"
    private val urlForGetTempAndHum = "https://dt.miet.ru/ppo_it/api/temp_hum/"
    private val urlForWindowOpen = "https://dt.miet.ru/ppo_it/api/fork_drive"
    private val urlForGlobalWatering = "https://dt.miet.ru/ppo_it/api/total_hum"
    private val urlForWateringControl = "https://dt.miet.ru/ppo_it/api/watering"
    private val queue = Volley.newRequestQueue(context)

    fun getSoilHum() {
        queue.start()
        for (id in 1..6) {
            val request = StringRequest(
                Request.Method.GET,
                urlForGetSoilHum + "$id",
                { response ->
                    val mapJson = getFurrowHumidity(response)
                    val soilHum = SoilHum(
                        mapJson["id"]!!.toByte(),
                        mapJson["humidity"]!!
                    )
                    ListForData.SoilHumList.add(soilHum)
                },
                { }
            )
            queue.add(request)
        }

    }

    fun getTempAndHum() {
        val queue = Volley.newRequestQueue(context)
        for (id in 1..4) {
            val request = StringRequest(
                Request.Method.GET,
                urlForGetTempAndHum + "$id",
                { response ->
                    val mapJson = getGreenhouseSensorData(response)
                    val tempAndHum = TempAndHum(
                        mapJson["id"]!!.toByte(),
                        mapJson["temp"]!!,
                        mapJson["hum"]!!
                    )
                    ListForData.TempAndHumList.add(tempAndHum)
                }, {}
            )
            queue.add(request)

        }
    }

    fun changeWindowState(state: Byte) {
        val queue = Volley.newRequestQueue(context)

        val request = StringRequest(
            Request.Method.PATCH,
            "$urlForWindowOpen?state=$state",
            {}, {}
        )
        queue.add(request)
    }

    fun changeFurrowState(id: Byte, state: Byte) {
        val queue = Volley.newRequestQueue(context)
        val request = StringRequest(
            Request.Method.PATCH,
            "$urlForWateringControl?id=$id&state=$state",
            {}, {}
        )
        queue.add(request)
    }

    fun changeGlobalWateringState(state: Byte) {
        val queue = Volley.newRequestQueue(context)
        val request = StringRequest(Request.Method.PATCH,
            "$urlForGlobalWatering?state=$state",
            {}, {}
        )
        queue.add(request)
    }

    private fun getGreenhouseSensorData(jsonString: String): Map<String, Number> {
        val json = JSONObject(jsonString)

        return mapOf(
            "id" to json.getString("id").toByte(),
            "temp" to json.getString("temperature").toFloat(),
            "hum" to json.getString("humidity").toFloat()
        )
    }

    private fun getFurrowHumidity(jsonString: String): Map<String, Number> {
        val json = JSONObject(jsonString)

        return mapOf(
            "id" to json.getString("id").toByte(),
            "humidity" to json.getString("humidity").toFloat()
        )
    }
}