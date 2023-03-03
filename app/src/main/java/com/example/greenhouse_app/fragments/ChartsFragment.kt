package com.example.greenhouse_app.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.LayoutManager
import com.example.greenhouse_app.R
import com.example.greenhouse_app.dataClasses.ListForData
import com.example.greenhouse_app.databinding.FragmentChartsBinding
import com.example.greenhouse_app.recyclerView.DataAdapter


private lateinit var adapter: DataAdapter
private lateinit var recyclerView: RecyclerView



class ChartsFragment : Fragment() {

    private lateinit var binding: FragmentChartsBinding

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentChartsBinding.inflate(inflater, container, false)
        val layoutManager = LinearLayoutManager(context)
        recyclerView = binding.rvDataFromSensors
        recyclerView.layoutManager = layoutManager
//        recyclerView.setHasFixedSize(true)
        adapter = DataAdapter(
            ListForData.EverySoilHumDataList, ListForData.EveryTempAndHumDataList
        )

        recyclerView.adapter = adapter




        return binding.root
    }


}