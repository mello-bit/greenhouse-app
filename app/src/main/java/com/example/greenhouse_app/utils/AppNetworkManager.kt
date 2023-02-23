package com.example.greenhouse_app.utils

import android.content.Context
import android.util.Log
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import org.json.JSONObject

class AppNetworkManager(private val context: Context?) {

    private val urlForGetSoilHum: String = "https://dt.miet.ru/ppo_it/api/hum/"
    private val urlForGetTempAndHum: String = "https://dt.miet.ru/ppo_it/api/temp_hum/"
    private val urlForPatch: String = "https://dt.miet.ru/ppo_it/api/fork_drive/"

    fun getSoilHum() {
        if (context != null) {
            val queue = Volley.newRequestQueue(context)
            for (id in 1..6) {
                val request = StringRequest(
                    Request.Method.GET,
                    urlForGetSoilHum + "$id",
                    { response ->
                        Log.d("MyLog", "${getFurrowHumidity(response)}")
                    },
                    { error ->
                        Log.e(null, "$error")
                    }
                )
                queue.add(request)
//            Log.d("MyLog", "Ok")
            }
        }
    }

    fun getTempAndHum(id: Int) {
        val queue = Volley.newRequestQueue(context)
        val request = StringRequest(
            Request.Method.GET,
            urlForGetTempAndHum + "$id",
            { response ->
                Log.d("MtLog", "${getGreenhouseSensorData(response)}")
                Log.d("MyLog", "Result: $response")
            },
            { error ->
                Log.e(null, "$error")
            }
        )
        queue.add(request)
        Log.d("MyLog", "Ok")
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