package com.example.greenhouse_app.utils

class RequestHandler {
    private val urlForGetSoilHum: String = "https://dt.miet.ru/ppo_it/api/hum/"
    private val urlForGetTempAndHum: String = "https://dt.miet.ru/ppo_it/api/temp_hum/"
    private val urlForPatch: String = "https://dt.miet.ru/ppo_it/api/fork_drive/"


    /**
     * This function will send get request to url: https://dt.miet.ru/ppo_it/api/hum/
     * @return The Map with key: Id and value:
     * */
    fun getRequest(): Map<Int, Int> {
        val r = mutableMapOf<Int, Int>()
        r[1] = 6
        return r
    }
}