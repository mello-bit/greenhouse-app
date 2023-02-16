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
import com.example.greenhouse_app.databinding.FragmentSettingsBinding
import com.example.greenhouse_app.utils.NetworkManager
import org.json.JSONObject

open class SettingsFragment : Fragment() {
    
    private val urlForGetSoilHum: String = "https://dt.miet.ru/ppo_it/api/hum/"
    private val urlForGetTempAndHum: String = "https://dt.miet.ru/ppo_it/api/temp_hum/"
    private val urlForPatch: String = "https://dt.miet.ru/ppo_it/api/fork_drive/"

    private lateinit var networkManager: NetworkManager
    private lateinit var binding: FragmentSettingsBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_settings, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        networkManager = NetworkManager(context)
        binding = FragmentSettingsBinding.inflate(layoutInflater)

        binding.saveChages.setOnClickListener {
            Log.d("Deb", "Ok")
            val bt = binding.boundaryTempValue.text
            val ht = binding.boundaryHumValue.text

            Toast.makeText(requireContext(), "$bt, $ht", Toast.LENGTH_SHORT).show()
        }
        networkManager.getSoilHum(1)
        networkManager.getTempAndHum(3)
    }


}