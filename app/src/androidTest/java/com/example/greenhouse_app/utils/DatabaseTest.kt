package com.example.greenhouse_app.utils

import android.content.Context
import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.util.*

@RunWith(AndroidJUnit4::class)
class DatabaseTest {
    private lateinit var context: Context
    private lateinit var database: AppDatabase
    private lateinit var dao: SensorDao

    @Before
    fun setUp() {
        context = InstrumentationRegistry.getInstrumentation().targetContext
        database = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        dao = database.SensorDao()
    }

    @Test
    fun testInsertData() = runBlocking{
        val sensorData = SensorData(
            id = 888,
            createdAt = "2022-03-17 10:30:00",
            greenhouse_temperature = 25.0f,
            greenhouse_humidity = 60.0f,
            furrow1_humidity = 40.0f,
            furrow2_humidity = 50.0f,
            furrow3_humidity = 45.0f,
            furrow4_humidity = 55.0f,
            furrow5_humidity = 60.0f,
            furrow6_humidity = 50.0f
        )
        val wasBefore = dao.getCount()

        dao.insertData(sensorData)
        val sensorDataFromDb = dao.getLatestSensorData()
        dao.deleteDataById(sensorData.id)

        assertEquals(sensorData, sensorDataFromDb)
        assertEquals(wasBefore, dao.getCount())
    }

    @Test
    fun testGetSensorDataForDate() = runBlocking {
        val sensorData1 = SensorData(
            id = 999,
            createdAt = "2022-03-17 10:15:00",
            greenhouse_temperature = 25.0f,
            greenhouse_humidity = 80.0f,
            furrow1_humidity = 60.0f,
            furrow2_humidity = 70.0f,
            furrow3_humidity = 75.0f,
            furrow4_humidity = 80.0f,
            furrow5_humidity = 85.0f,
            furrow6_humidity = 90.0f
        )

        val sensorData2 = SensorData(
            id = 1000,
            createdAt = "2022-03-18 12:30:00",
            greenhouse_temperature = 26.0f,
            greenhouse_humidity = 81.0f,
            furrow1_humidity = 61.0f,
            furrow2_humidity = 71.0f,
            furrow3_humidity = 76.0f,
            furrow4_humidity = 81.0f,
            furrow5_humidity = 86.0f,
            furrow6_humidity = 91.0f
        )

        dao.insertData(sensorData1)
        dao.insertData(sensorData2)

        val sensorDataForDate = dao.getSensorDataForDate("2022-03-18")

        assertEquals(sensorDataForDate.size, 1)
        assertEquals(sensorDataForDate[0], sensorData2)

        dao.deleteDataById(sensorData1.id)
        dao.deleteDataById(sensorData2.id)
    }

    @Test
    fun testGetSensorDataBetweenDates() = runBlocking {
        val sensorData = mutableListOf<SensorData>()

        setOf(
            "2022-01-01 12:00:00",
            "2022-01-02 12:00:00",
            "2022-01-03 12:00:00",
            "2022-01-04 12:00:00"
        ).forEach { data -> sensorData.add(SensorData(
            createdAt = data,
            greenhouse_temperature = 20.0f,
            greenhouse_humidity = 60.0f,
            furrow1_humidity = 50.0f,
            furrow2_humidity = 40.0f,
            furrow3_humidity = 30.0f,
            furrow4_humidity = 20.0f,
            furrow5_humidity = 10.0f,
            furrow6_humidity = 0.0f
        ))}

        val sensorData1 = sensorData.removeAt(0)
        val sensorData2 = sensorData.removeAt(0)
        val sensorData3 = sensorData.removeAt(0)
        val sensorData4 = sensorData.removeAt(0)

        dao.insertData(sensorData1)
        dao.insertData(sensorData2)
        dao.insertData(sensorData3)
        dao.insertData(sensorData4)

        val startDate = "2022-01-02 00:00:00"
        val endDate = "2022-01-03 23:59:59"
        val result = database.SensorDao().getSensorDataBetweenDates(startDate, endDate)

        // Check that the correct sensor data was returned
        assertEquals(2, result.size)
        assertEquals(sensorData2.createdAt, result[0].createdAt)
        assertEquals(sensorData3.createdAt, result[1].createdAt)
    }


    @After
    fun tearDown() {
        database.close()
    }
}