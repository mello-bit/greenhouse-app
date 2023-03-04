package com.example.greenhouse_app.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.greenhouse_app.dataClasses.ListForData
import com.example.greenhouse_app.databinding.FragmentChartsBinding
import com.example.greenhouse_app.recyclerView.DataAdapter



class ChartsFragment : Fragment() {

    private lateinit var binding: FragmentChartsBinding
    private lateinit var recyclerView: RecyclerView
    private lateinit var handler: Handler
    private val myAdapter by lazy { DataAdapter() }

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentChartsBinding.inflate(inflater, container, false)
        handler = Handler(Looper.getMainLooper())

        val layoutManager = LinearLayoutManager(context)

        recyclerView = binding.rvDataFromSensors
        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = myAdapter

        myAdapter.setData(ListForData.EverySoilHumDataList)
        myAdapter.notifyDataSetChanged()

        handler.post(object : Runnable {
            override fun run() {
                myAdapter.setData(ListForData.EverySoilHumDataList)
                myAdapter.notifyDataSetChanged()
                recyclerView.scrollToPosition(ListForData.EverySoilHumDataList.size - 1)
                handler.postDelayed(this, 200)
            }
        })

        return binding.root
    }

}