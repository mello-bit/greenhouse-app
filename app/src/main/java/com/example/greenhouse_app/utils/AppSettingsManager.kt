package com.example.greenhouse_app.utils

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.util.Log

class AppSettingsManager {

    companion object {

        @SuppressLint("StaticFieldLeak")
        private lateinit var context: Context
        private lateinit var sharedPreferences: SharedPreferences

        fun initContext(application_context: Context) {
            context = application_context
            sharedPreferences = context.getSharedPreferences("Settings", Context.MODE_PRIVATE)
            Log.d("TempTag", "Initialized AppSettings")
        }


        fun saveData(key: String, value: String) {
            val editor = sharedPreferences.edit()
            editor.putString(key, value)

            editor.apply()
        }

        fun loadData(key: String): String? {
            return sharedPreferences.getString(key, "")
        }
    }
}