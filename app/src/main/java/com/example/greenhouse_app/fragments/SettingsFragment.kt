package com.example.greenhouse_app.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.greenhouse_app.dataClasses.ListForData
import com.example.greenhouse_app.databinding.FragmentSettingsBinding
import com.example.greenhouse_app.utils.AppSettingsManager
import com.example.greenhouse_app.utils.AppNetworkManager

open class SettingsFragment : Fragment() {

    private lateinit var networkManager: AppNetworkManager
    private lateinit var binding: FragmentSettingsBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSettingsBinding.inflate(inflater, container, false)

        networkManager = AppNetworkManager(context)

        if (AppSettingsManager.isAllDataExists) {
            binding.boundaryTempValue.setText(AppSettingsManager.loadData("tempValue"))
            binding.boundaryHumValue.setText(AppSettingsManager.loadData("humValue"))
            binding.boundarySoilHum.setText(AppSettingsManager.loadData("soilValue"))
        }

        binding.saveChanges.setOnClickListener {
            Toast.makeText(requireContext(), "Btn was clicked", Toast.LENGTH_SHORT).show()
            Log.d("TempTag", "saveChanges was clicked")

            AppSettingsManager.saveData(
                "tempValue",
                value = binding.boundaryTempValue.text.toString()
            )
            AppSettingsManager.saveData(
                "humValue",
                value = binding.boundaryHumValue.text.toString()
            )
            AppSettingsManager.saveData(
                "soilValue",
                value = binding.boundarySoilHum.text.toString()
            )
        }

        networkManager.getSoilHum()

//        networkManager.getTempAndHum()
        Log.d("MyLog", ListForData.SoilHumList.toString())

        return binding.root
    }

}