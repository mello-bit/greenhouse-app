package com.example.greenhouse_app


class Furrow(val id: Byte, var humidity: Byte = 0, var is_watering: Boolean = false) {
//    WARNING. THERE IS NO WAY TO REMOVE CALLBACKS. MODIFY THE CLASS IN CASE IT WILL HAPPEN OCCASIONALY

    private var wateringStartConnections = mutableSetOf<() -> Unit>()
    private var wateringEndConnections = mutableSetOf<() -> Unit>()

    fun startWatering() {
        is_watering = true
        wateringStartConnections.forEach{it()}
    }

    fun stopWatering() {
        is_watering = false
        wateringEndConnections.forEach{it()}
    }

    fun onWateringStarted(callback: () -> Unit) = wateringStartConnections.add(callback)
    fun onWateringEnded(callback: () -> Unit) = wateringEndConnections.add(callback)
}


fun main() {
    val testClass = Furrow(1, 15, false)

    testClass.onWateringStarted { println("1: Watering started!") }
    testClass.onWateringStarted { println("2: Watering started!") }
    testClass.startWatering()
}