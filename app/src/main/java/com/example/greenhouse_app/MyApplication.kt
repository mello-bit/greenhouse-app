package com.example.greenhouse_app

import android.app.Application
import android.util.Log
import com.example.greenhouse_app.utils.AppSettingsManager

class MyApplication : Application() {

    override fun onCreate() {
        Log.d("TempTag", "Started application")
        AppSettingsManager.initContext(applicationContext)
        super.onCreate()

        // Initialize the Greenhouse instance
//        Greenhouse.initialize(this)
//        val ap = AppSettingsManager()
//        ap.initContext(con)
    }

    override fun onTerminate() {
//        Greenhouse.getInstance().saveState()
        super.onTerminate()
    }
}