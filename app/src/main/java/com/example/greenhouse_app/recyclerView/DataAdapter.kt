package com.example.greenhouse_app.recyclerView

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.forEach
import androidx.fragment.app.Fragment
import androidx.fragment.app.findFragment
import androidx.recyclerview.widget.RecyclerView
import com.example.greenhouse_app.R
import com.example.greenhouse_app.dataClasses.SoilHum
import com.example.greenhouse_app.dataClasses.TempAndHum
import com.example.greenhouse_app.fragments.findViewWhichIs
import java.security.PrivateKey

class DataAdapter(
    private val furrowData: List<List<SoilHum>>,
    private val tempAndHum: List<List<TempAndHum>>
) : RecyclerView.Adapter<DataAdapter.DataViewHolder>() {

    inner class DataViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val llFurrowData = itemView.findViewById<LinearLayout>(R.id.llFurrowData)
        val llTempData = itemView.findViewById<LinearLayout>(R.id.llTempData)
        val llHumData = itemView.findViewById<LinearLayout>(R.id.llHumData)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DataViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.card_table, parent, false)

        return DataViewHolder(view)
    }

    override fun getItemCount(): Int {
        return furrowData.size + tempAndHum.size
    }

    override fun onBindViewHolder(holder: DataViewHolder, position: Int) {
//        Log.d("LogTr", position.toString())
        if (position < furrowData.size){
            val currentItem = furrowData[position]

            Log.d("LogTr", currentItem.toString())
            for (i in 0 until holder.llFurrowData.childCount) {
                val view = holder.llFurrowData.getChildAt(i) as TextView
                view.text = currentItem[i].humidity.toString()
            }
            return

        }
        return
    }


}