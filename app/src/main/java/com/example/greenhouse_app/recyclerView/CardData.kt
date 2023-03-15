package com.example.greenhouse_app.recyclerView

import com.example.greenhouse_app.dataClasses.SoilHum
import com.example.greenhouse_app.dataClasses.TempAndHum

data class CardData(
    val furrowHumidityList: MutableList<SoilHum>,
    val temperatureAndHumidityList: MutableList<TempAndHum>,
)
