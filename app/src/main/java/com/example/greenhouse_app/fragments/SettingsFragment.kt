@file:Suppress("DEPRECATION")

package com.example.greenhouse_app.fragments


import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.greenhouse_app.databinding.FragmentSettingsBinding

class SettingsFragment : Fragment() {
    private lateinit var binding: FragmentSettingsBinding
    private lateinit var sharedPreferences: SharedPreferences
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        sharedPreferences = requireActivity().getPreferences(Context.MODE_PRIVATE)

    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSettingsBinding.inflate(inflater, container, false)
        binding.saveChages.setOnClickListener {
            val tempV = binding.boundaryTempValue.text
            val humV = binding.boundaryHumValue.text

            saveData("tempV", tempV.toString())
            saveData("humV", humV.toString())
        }

        binding.extremeControlBtn.setOnClickListener {
            Toast.makeText(requireContext(),
                "Вы включили режим экстренного управления", Toast.LENGTH_SHORT)
                .show()
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