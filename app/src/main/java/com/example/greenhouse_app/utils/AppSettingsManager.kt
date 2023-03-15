package com.example.greenhouse_app.utils

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.example.greenhouse_app.MyApplication

class AppSettingsManager {

    companion object {

        var isAllBoundaryDataExists = false
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

        fun checkThatDataExists(key: String): Boolean {
            if (sharedPreferences.contains(key)) return true
            return false
        }

    }
}