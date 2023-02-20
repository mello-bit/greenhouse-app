package com.example.greenhouse_app.fragments

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.greenhouse_app.R
import com.example.greenhouse_app.databinding.FragmentNotificationBinding
import com.example.greenhouse_app.databinding.FragmentSettingsBinding
import com.example.greenhouse_app.utils.AppSettingsManager


class NotificationFragment : Fragment() {
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var binding: FragmentNotificationBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentNotificationBinding.inflate(inflater, container, false)

        binding.loadDtNt.setOnClickListener {
            val tempValue = AppSettingsManager.loadData("tempValue").toString()
            val humValue = AppSettingsManager.loadData("humValue").toString()
            val soilValue = AppSettingsManager.loadData("soilValue").toString()

            Toast.makeText(requireContext(),
                "$tempValue $humValue $soilValue",
                Toast.LENGTH_SHORT
            ).show()
        }

        return binding.root
    }


    fun saveData(key: String, value: String) {
        val editor = sharedPreferences.edit()

        editor.putString(key, value)

        editor.apply()
    }

    fun loadData(key: String): String? {
        return sharedPreferences.getString(key, "")
    }
}