package com.example.greenhouse_app.utils

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.util.Log

class AppSettingsManager {

    companion object {

        var isAllBoundaryDataExists = false
        @SuppressLint("StaticFieldLeak")
        private lateinit var context: Context
        private lateinit var sharedPreferences: SharedPreferences

        fun initContext(application_context: Context) {
            context = application_context
            sharedPreferences = context.getSharedPreferences("Settings", Context.MODE_PRIVATE)
            Log.d("TempTag", "Initialized AppSettings")
        }

        /**
         * This function will save your data by key
         * @param key
         * @param value
         */
        fun saveData(key: String, value: String) {
            val editor = sharedPreferences.edit()
            editor.putString(key, value)

            editor.apply()
        }

        /**
         * This function will load your data for this key
         * @param key to your data
         * @return your data for this key
         */
        fun loadData(key: String): String? {
            return sharedPreferences.getString(key, "")
        }

        /**
         * This function will check that data with this key exists
         * @param key to your data
         * @return true if your data with this key exists else false
         */
        fun checkThatDataExists(key: String): Boolean {
            if (sharedPreferences.contains(key)) return true
            return false
        }

    }
}