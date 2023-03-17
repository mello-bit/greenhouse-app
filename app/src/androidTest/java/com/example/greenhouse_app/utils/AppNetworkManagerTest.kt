package com.example.greenhouse_app.utils

import android.content.Context
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.greenhouse_app.dataClasses.ListForData
import com.example.greenhouse_app.dataClasses.SoilHum
import com.example.greenhouse_app.dataClasses.TempAndHum
import org.json.JSONObject
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

@RunWith(AndroidJUnit4::class)
class AppNetworkManagerTest {
    private val urlForGetSoilHum = "https://dt.miet.ru/ppo_it/api/hum/"
    private val urlForGetTempAndHum = "https://dt.miet.ru/ppo_it/api/temp_hum/"
    private val urlForWindowOpen = "https://dt.miet.ru/ppo_it/api/fork_drive"
    private val urlForGlobalWatering = "https://dt.miet.ru/ppo_it/api/total_hum"
    private val urlForWateringControl = "https://dt.miet.ru/ppo_it/api/watering"
    private lateinit var context: Context

    @Before
    fun setUp() {
        context = InstrumentationRegistry.getInstrumentation().targetContext
    }

    @Test
    fun testGetSoilHum() {
        val queue = Volley.newRequestQueue(context)
        val latch = CountDownLatch(6)

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
                    latch.countDown()
                },
                { _ ->
                    latch.countDown()
                }
            )
            queue.add(request)
        }

        val success = latch.await(10, TimeUnit.SECONDS)
        assertEquals(true, success)
        assertEquals(6, ListForData.SoilHumList.size)
        ListForData.SoilHumList.clear()
    }

    @Test
    fun testGetTempAndHum() {
        val queue = Volley.newRequestQueue(context)
        val latch = CountDownLatch(4)

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
                    latch.countDown()
                },
                { _ ->
                    // Handle error here
                    latch.countDown()
                }
            )
            queue.add(request)
        }

        val success = latch.await(10, TimeUnit.SECONDS)
        assertEquals(true, success)
        assertEquals(4, ListForData.TempAndHumList.size)
        ListForData.TempAndHumList.clear()
    }

    @Test
    fun testChangeWindowState() {
        val latch = CountDownLatch(3)
        val queue = Volley.newRequestQueue(context)
        var succeded = 0

        val request = StringRequest(
            Request.Method.PATCH,
            "$urlForWindowOpen?state=1",
            {succeded += 1; latch.countDown()}, {fail("testChangeWindowState() with state=1 failed when shouldn't")}
        )

        val request2 = StringRequest(
            Request.Method.PATCH,
            "$urlForWindowOpen?state=0",
            {succeded += 1; latch.countDown()}, {fail("testChangeWindowState() with state=0 failed when shouldn't")}
        )

        val request3 = StringRequest(
            Request.Method.PATCH,
            "$urlForWindowOpen?state=77",
            {fail("testChangeWindowState() with state=77 succeeded when shouldn't")}, {succeded += 1; latch.countDown()}
        )

        queue.add(request)
        queue.add(request2)
        queue.add(request3)

        val success = latch.await(10, TimeUnit.SECONDS)
        assertEquals(true, success)
        assertEquals(3, succeded)
    }

    @Test
    fun testChangeHumidifierState() {
        val latch = CountDownLatch(3)
        val queue = Volley.newRequestQueue(context)
        var succeded = 0

        val request = StringRequest(Request.Method.PATCH,
            "$urlForGlobalWatering?state=1",
            {succeded += 1; latch.countDown()}, {fail("testChangeHumidifierState() failed with state=1 when shouldn't")}
        )

        val request2 = StringRequest(Request.Method.PATCH,
            "$urlForGlobalWatering?state=0",
            {succeded += 1; latch.countDown()}, {fail("testChangeHumidifierState() failed with state=0 when shouldn't")}
        )

        val request3 = StringRequest(Request.Method.PATCH,
            "$urlForGlobalWatering?state=4324",
            {fail("testChangeHumidifierState() succeeded with state=4324 when shouldn't")}, {succeded += 1; latch.countDown()}
        )

        queue.add(request)
        queue.add(request2)
        queue.add(request3)

        val success = latch.await(10, TimeUnit.SECONDS)
        assertEquals(true, success)
        assertEquals(3, succeded)
    }

    @Test
    fun testChangeFurrowState() {
        val latch = CountDownLatch(12)

        val queue = Volley.newRequestQueue(context)
        var succeded = 0

        for (id in 1..6) {
            for (state in 0..1) {
                val request = StringRequest(
                    Request.Method.PATCH,
                    "$urlForWateringControl?id=$id&state=$state",
                    {succeded += 1; latch.countDown()}, {fail("testChangeFurrowState() failed when shouldn't")}
                )
                queue.add(request)
            }
        }

        val success = latch.await(10, TimeUnit.SECONDS)
        assertEquals(true, success)
        assertEquals(12, succeded)

        val latch2 = CountDownLatch(18)
        succeded = 0

        for (id in 7..9) {
            for (state in -1..4) {
                val request = StringRequest(
                    Request.Method.PATCH,
                    "$urlForWateringControl?id=$id&state=$state",
                    {fail("testChangeFurrowState() succeeded when shouldn't")}, {succeded += 1; latch2.countDown()}
                )
                queue.add(request)
            }
        }

        val success2 = latch2.await(10, TimeUnit.SECONDS)
        assertEquals(true, success2)
        assertEquals(18, succeded)
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