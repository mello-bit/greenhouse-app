package com.example.greenhouse_app.utils

import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.After
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import java.util.*

@RunWith(AndroidJUnit4::class)
class SharedPreferencesTest {
    private val key1 = "SOME_TEST_KEY_ONE"
    private val key2 = "SOME_TEST_KEY_TWO"
    private val key3 = "SOME_TEST_KEY_THREE"
    private val key4 = "SOME_TEST_KEY_FOUR"
    private val key5 = "SOME_TEST_KEY_FIVE"

    @Test
    fun testWriteAndRead() {
        AppSettingsManager.saveData(key1, key1)
        AppSettingsManager.saveData(key2, key2)

        assertEquals(AppSettingsManager.loadData(key1), key1)
        assertEquals(AppSettingsManager.loadData(key2), key2)
    }

    @Test
    fun testStackedWritesAndRead() {
        AppSettingsManager.saveData(key3, key3)
        AppSettingsManager.saveData(key3, key4)
        AppSettingsManager.saveData(key4, key4)
        AppSettingsManager.saveData(key4, key1)
        AppSettingsManager.saveData(key5, key5)


        assertEquals(AppSettingsManager.loadData(key3), key4)
        assertEquals(AppSettingsManager.loadData(key4), key1)
        assertEquals(key5, key5)
    }

    @After
    fun tearDown() {
        setOf(key1, key2, key3, key4, key5)
            .forEach {key -> AppSettingsManager.resetData(key) }
    }
}