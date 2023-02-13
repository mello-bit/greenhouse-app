package com.example.greenhouse_app

//import android.content.Context
//import org.json.JSONArray
//import org.json.JSONObject
//
//
//object Greenhouse {
//    private var furrows: MutableList<Furrow> = mutableListOf()
//
//    fun initialize(context: Context) {
//        if (furrows.isEmpty()) {
//            loadState(context)
//        }
//    }
//
//    fun addFurrow(furrow: Furrow) {
//        furrows.add(furrow)
//    }
//
//    fun getFurrows(): List<Furrow> {
//        return furrows
//    }
//
//    fun clearFurrows() {
//        furrows.clear()
//    }
//
//    fun saveState(context: Context) {
//        val sharedPreferences = context.getSharedPreferences("Greenhouse", Context.MODE_PRIVATE)
//        val editor = sharedPreferences.edit()
//
//        // Convert the furrows list to a JSON array
//        val furrowsJsonArray = JSONArray()
//        for (furrow in furrows) {
//            val furrowJsonObject = JSONObject()
//            furrowJsonObject.put("id", furrow.id.toInt())
//            furrowJsonObject.put("humidity", furrow.humidity.toInt())
//            furrowJsonObject.put("is_watering", furrow.is_watering)
//            furrowsJsonArray.put(furrowJsonObject)
//        }
//
//        // Put the JSON array in the SharedPreferences editor
//        editor.putString("furrows", furrowsJsonArray.toString())
//
//        // Commit the changes
//        editor.apply()
//    }
//
//    private fun loadState(context: Context) {
//        val sharedPreferences = context.getSharedPreferences("Greenhouse", Context.MODE_PRIVATE)
//
//        // Get the furrows JSON array from SharedPreferences
//        val furrowsJsonArrayString = sharedPreferences.getString("furrows", null)
//
//        // Parse the JSON array into a list of Furrow objects
//        if (furrowsJsonArrayString != null) {
//            val furrowsJsonArray = JSONArray(furrowsJsonArrayString)
//            val loadedFurrows: MutableList<Furrow> = mutableListOf()
//
//            for (i in 0 until furrowsJsonArray.length()) {
//                val furrowJsonObject = furrowsJsonArray.getJSONObject(i)
//                val id = furrowJsonObject.getInt("id").toByte()
//                val humidity = furrowJsonObject.getInt("humidity").toByte()
//                val isWatering = furrowJsonObject.getBoolean("is_watering")
//                val furrow = Furrow(id, humidity, isWatering)
//                loadedFurrows.add(furrow)
//            }
//
//            // Set the furrows list to the loaded list
//            furrows.clear()
//            furrows.addAll(loadedFurrows)
//        }
//    }
//}