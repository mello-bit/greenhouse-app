package com.example.greenhouse_app.recyclerView

import androidx.recyclerview.widget.DiffUtil
import com.example.greenhouse_app.dataClasses.AllData

class AppDiffUtil(
    private val oldList: List<AllData>,
    private val newList: List<AllData>
): DiffUtil.Callback() {

    override fun getOldListSize(): Int {
        return oldList.size
    }

    override fun getNewListSize(): Int {
        return newList.size
    }

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition].soilHum1 == newList[newItemPosition].soilHum1
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return when {
            oldList[oldItemPosition].soilHum1 != newList[newItemPosition].soilHum1 -> {
                false
            }
            oldList[oldItemPosition].soilHum2 != newList[newItemPosition].soilHum2 -> {
                false
            }
            else -> true
        }
    }
}