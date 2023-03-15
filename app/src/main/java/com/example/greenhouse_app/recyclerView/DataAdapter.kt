package com.example.greenhouse_app.recyclerView

import android.view.LayoutInflater
import android.view.ViewGroup

import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.greenhouse_app.dataClasses.AllData
import com.example.greenhouse_app.databinding.CardTableBinding

class DataAdapter : RecyclerView.Adapter<DataAdapter.DataViewHolder>() {

    private var oldlist = emptyList<AllData>()

    inner class DataViewHolder(val binding: CardTableBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DataViewHolder {
        val binding = CardTableBinding.inflate(LayoutInflater
            .from(parent.context), parent, false)

        return DataViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return oldlist.size
    }

    override fun onBindViewHolder(holder: DataViewHolder, position: Int) {
        for (i in 0 until holder.binding.llFurrowData.childCount) {
            val view = holder.binding.llFurrowData.getChildAt(i) as TextView
            when (i) {
                0 -> view.text = oldlist[position].soilHum1.toString()
                1 -> view.text = oldlist[position].soilHum2.toString()
                2 -> view.text = oldlist[position].soilHum3.toString()
                3 -> view.text = oldlist[position].soilHum4.toString()
                4 -> view.text = oldlist[position].soilHum5.toString()
                5 -> view.text = oldlist[position].soilHum6.toString()
            }
        }

        for (i in 1 until holder.binding.llTempData.childCount) {
            val tempView = holder.binding.llTempData.getChildAt(i) as TextView
            val humView = holder.binding.llHumData.getChildAt(i) as TextView

            when (i) {
                1 -> {
                    tempView.text = oldlist[position].tempGreenhouse1.toString()
                    humView.text = oldlist[position].humGreenhouse1.toString()
                }
                2 -> {
                    tempView.text = oldlist[position].tempGreenhouse2.toString()
                    humView.text = oldlist[position].tempGreenhouse2.toString()
                }
                3 -> {
                    tempView.text = oldlist[position].tempGreenhouse3.toString()
                    humView.text = oldlist[position].humGreenhouse3.toString()
                }
                4 -> {
                    tempView.text = oldlist[position].tempGreenhouse4.toString()
                    humView.text = oldlist[position].humGreenhouse4.toString()
                }
            }
        }

    }

    fun setData(newSoilHumList: List<AllData>) {
        val diffUtil = AppDiffUtil(oldlist, newSoilHumList)
        val diffResults = DiffUtil.calculateDiff(diffUtil)
        oldlist = newSoilHumList
        diffResults.dispatchUpdatesTo(this)
    }

}