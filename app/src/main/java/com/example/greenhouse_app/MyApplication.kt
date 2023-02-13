package com.example.greenhouse_app
//
//import android.app.Application
//
//class MyApplication : Application() {
//
//    override fun onCreate() {
//        super.onCreate()
//
//        // Initialize the Greenhouse instance
//        Greenhouse.initialize(this)
//    }
//
//    override fun onTerminate() {
//        Greenhouse.getInstance().saveState()
//        super.onTerminate()
//    }
//}