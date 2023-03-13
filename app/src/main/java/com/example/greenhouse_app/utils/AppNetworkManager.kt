package com.example.greenhouse_app.utils

import android.content.Context
import android.util.Log
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
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
    /*
        urlForGetSoilHum: Влажность с датчиков почвы
        urlForGetTempAnHum: Влажность и температура с 4х угловых датчиков теплицы
        urlForWindowOpen: (PATCH) - Открытие/Закрытие форточек
        urlForGlobalWatering: (PATCH) - Управление единой системой увлажнения
        urlForWateringControl: (PATCH) - Вкл/выкл увлажнение бороздки
     */

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
                { error ->
                    Log.e(null, "$error")
                }
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
                },
                { error ->
                    Log.d("MyLog", "$error")
                }
            )
            queue.add(request)

        }
    }

    //изменение статуса форточка
    fun changeWindowState(state: Byte) {
        // urlForWindowOpen: Использовать это.
        // state: Или 1, или 0. Обрабатывать не требуется.
        val queue = Volley.newRequestQueue(context)

        val request = StringRequest(
            Request.Method.PATCH,
            "$urlForWindowOpen?state=$state",
            {response ->
                Log.d("ResponseTag", response.toString())
            },
            {error ->
                Log.d("ResponseTag", error.toString())
            }
        )
        queue.add(request)
    }

    fun changeFurrowState(id: Byte, state: Byte) {
        val queue = Volley.newRequestQueue(context)
        val request = StringRequest(
            Request.Method.PATCH,
            "$urlForWateringControl?id=$id&state=$state",
            { response ->
                Log.d("ResponseTag", response.toString() + id.toString())
            },
            { error ->
                Log.d("ResponseTag", error.toString() + id.toString())
            }
        )
        queue.add(request)
    }

    fun changeGlobalWateringState(state: Byte) {
        val queue = Volley.newRequestQueue(context)
        val request = StringRequest(Request.Method.PATCH,
            "$urlForGlobalWatering?state=$state",
            {response ->
                Log.d("ResponseTag", response.toString())
            },
            {error ->
                Log.d("ResponseTag", error.toString())
            }
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