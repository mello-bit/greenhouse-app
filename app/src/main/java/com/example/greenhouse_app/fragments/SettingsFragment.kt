package com.example.greenhouse_app.fragments

import android.content.SharedPreferences.Editor
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.greenhouse_app.R
import com.example.greenhouse_app.databinding.FragmentSettingsBinding

open class SettingsFragment : Fragment() {
    
    private val urlForGetSoilHum: String = "https://dt.miet.ru/ppo_it/api/hum/"
    private val urlForGetTempAndHum: String = "https://dt.miet.ru/ppo_it/api/temp_hum/"
    private val urlForPatch: String = "https://dt.miet.ru/ppo_it/api/fork_drive/"

    private lateinit var binding: FragmentSettingsBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_settings, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentSettingsBinding.inflate(layoutInflater)
        binding.saveChages.setOnClickListener {
            Log.d("Deb", "Ok")
            val bt = binding.boundaryTempValue.text
            val ht = binding.boundaryHumValue.text

            Toast.makeText(requireContext(), "$bt, $ht", Toast.LENGTH_SHORT).show()
        }
        getSoilHum(1)
        getTempAndHum(3)
    }

    private fun getSoilHum(id: Int) {
        val queue = Volley.newRequestQueue(context)
        val request = StringRequest(
            Request.Method.GET,
            urlForGetSoilHum + "$id",
            {response ->
                Log.d("MyLog", "Result: $response")
            },
            {error ->
                Log.d("Er", "$error")
            }
        )
        queue.add(request)
        Log.d("MyLog", "Ok")
    }

    private fun getTempAndHum(id: Int) {
        val queue = Volley.newRequestQueue(context)
        val request = StringRequest(
            Request.Method.GET,
            urlForGetTempAndHum + "$id",
            {response ->
                Log.d("MyLog", "Result: $response")
            },
            {error ->
                Log.d("Er", "$error")
            }
        )
        queue.add(request)
        Log.d("MyLog", "Ok")
    }
}