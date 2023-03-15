package com.example.greenhouse_app.dataClasses

import com.github.mikephil.charting.data.Entry

class ListForData {
    companion object {
        val EverySoilHumDataList = mutableListOf<AllData>()
        val TempAndHumList = mutableListOf<TempAndHum>()
        val SoilHumList = mutableListOf<SoilHum>()
        val currentDateList = mutableListOf<String>()
        val soilHum1List = mutableListOf<Entry>()
        val soilHum2List = mutableListOf<Entry>()
        val soilHum3List = mutableListOf<Entry>()
        val soilHum4List = mutableListOf<Entry>()
        val soilHum5List = mutableListOf<Entry>()
        val soilHum6List = mutableListOf<Entry>()
        val averageSoilHumList = mutableListOf<Entry>()

        val tempList1 = mutableListOf<Entry>()
        val tempList2 = mutableListOf<Entry>()
        val tempList3 = mutableListOf<Entry>()
        val tempList4 = mutableListOf<Entry>()
        val tempAverageList = mutableListOf<Entry>()
        val humList1 = mutableListOf<Entry>()
        val humList2 = mutableListOf<Entry>()
        val humList3 = mutableListOf<Entry>()
        val humList4 = mutableListOf<Entry>()
        val humAverageList = mutableListOf<Entry>()
    }
}