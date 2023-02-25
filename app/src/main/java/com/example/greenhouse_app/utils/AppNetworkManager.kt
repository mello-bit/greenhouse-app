package com.example.greenhouse_app.utils

import android.content.Context
import android.util.Log
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.greenhouse_app.dataClasses.ListForData
import com.example.greenhouse_app.dataClasses.SoilHum
import com.example.greenhouse_app.dataClasses.TempAndHum
import org.json.JSONObject

class AppNetworkManager(private val context: Context?) {

    private val urlForGetSoilHum: String = "https://dt.miet.ru/ppo_it/api/hum/"
    private val urlForGetTempAndHum: String = "https://dt.miet.ru/ppo_it/api/temp_hum/"
    private val urlForPatch: String = "https://dt.miet.ru/ppo_it/api/fork_drive/"

    fun getSoilHum() {
        val queue = Volley.newRequestQueue(context)
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
                    if (ListForData.SoilHumList.size == 6) ListForData.SoilHumList.clear()
                    ListForData.SoilHumList.add(soilHum)
                },
                { error ->
                    Log.e(null, "$error")
                }
            )
            queue.add(request)
//            Log.d("MyLog", "Ok")
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
                    if (ListForData.TempAndHumList.size == 4) ListForData.TempAndHumList.clear()
                    ListForData.TempAndHumList.add(tempAndHum)
                },
                { error ->
                    Log.d("MyLog", "$error")
                }
            )
            queue.add(request)

        }
    }

    /**
     * @param id of sensor
     * @return Will return a map
     */
    private fun getGreenhouseSensorData(jsonString: String): Map<String, Number> {
        val json = JSONObject(jsonString)

        return mapOf(
            "id" to json.getString("id").toByte(),
            "temp" to json.getString("temperature").toFloat(),
            "hum" to json.getString("humidity").toFloat()
        )
    }

    /**
     * @param id of sensor
     * @return Will return a map
     */
    private fun getFurrowHumidity(jsonString: String): Map<String, Number> {
        val json = JSONObject(jsonString)

        return mapOf(
            "id" to json.getString("id").toByte(),
            "humidity" to json.getString("humidity").toFloat()
        )
    }
}