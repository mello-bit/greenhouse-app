package com.example.greenhouse_app.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.greenhouse_app.R
import com.example.greenhouse_app.databinding.FragmentNotificationBinding
import com.example.greenhouse_app.databinding.FragmentSettingsBinding
import com.example.greenhouse_app.utils.AppSettingsManager
import com.example.greenhouse_app.utils.NetworkManager
import kotlinx.coroutines.*
import org.json.JSONObject

open class SettingsFragment : Fragment() {

    private lateinit var networkManager: NetworkManager
    private lateinit var binding: FragmentSettingsBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSettingsBinding.inflate(inflater, container, false)

        networkManager = NetworkManager(context)
        binding = FragmentSettingsBinding.inflate(layoutInflater)

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

        CoroutineScope(Dispatchers.IO).launch {

            for (i in 0..4) {
                val soilHum1 = async { networkManager.getSoilHum(i) }
                Log.d("MyLog", "${soilHum1.await()}")
            }
        }
        return binding.root
    }

}