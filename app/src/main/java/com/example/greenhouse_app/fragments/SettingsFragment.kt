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
import com.example.greenhouse_app.R
import com.example.greenhouse_app.databinding.FragmentSettingsBinding

class SettingsFragment : Fragment() {
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


    }
}