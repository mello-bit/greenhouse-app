package com.example.greenhouse_app


//class Furrow(
//    var id: Byte,
//    var humidity: Byte,
//    is_watering: Boolean
//) {
//    private val wateringCallbacks = mutableMapOf<Connection, (Boolean) -> Unit>()
//
//    fun setOnWateringCallback(callback: (Boolean) -> Unit): Connection {
//        val connection = Connection()
//        wateringCallbacks[connection] = callback
//        return connection
//    }
//
//    var is_watering: Boolean = is_watering
//        set(value) {
//            field = value
//            wateringCallbacks.values.forEach { it(value) }
//        }
//
//    inner class Connection {
//        private var isConnected: Boolean = true
//
//        fun disconnect() {
//            wateringCallbacks.remove(this)
//            isConnected = false
//        }
//
//        fun isConnected(): Boolean {
//            return isConnected
//        }
//    }
//}
//
//fun main() {
//    val furrow = Furrow(1, 50, false)
//
//    val callbackToken = furrow.setOnWateringCallback { isWatering ->
//        println("1. Furrow is watering: $isWatering")
//    }
//
//    val callbackToken2 = furrow.setOnWateringCallback { isWatering ->
//        println("2. Furrow is watering: $isWatering")
//    }
//
//    // Call is_watering setter to trigger the callback function
//    furrow.is_watering = true // This will print "Furrow is watering: true"
//
//    // Disconnect the callback function
//    callbackToken.disconnect()
//
//    // Call is_watering setter again to trigger the callback function, but it won't be called since it's been disconnected
//    furrow.is_watering = false
//}
