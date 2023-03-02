package com.example.greenhouse_app.dataClasses

class ListForData {
    companion object {
        val EveryTempAndHumDataList = mutableListOf<MutableList<TempAndHum>>()
        val EverySoilHumDataList = mutableListOf<MutableList<SoilHum>>()
        val TempAndHumList = mutableSetOf<TempAndHum>()
        val SoilHumList = mutableSetOf<SoilHum>()
    }
}