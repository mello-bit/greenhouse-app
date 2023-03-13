package com.example.greenhouse_app.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.greenhouse_app.databinding.FragmentSettingsBinding
import com.example.greenhouse_app.utils.AppSettingsManager
import com.example.greenhouse_app.utils.AppNetworkManager

open class SettingsFragment : Fragment() {

//    private lateinit var networkManager: AppNetworkManager
    private lateinit var binding: FragmentSettingsBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSettingsBinding.inflate(inflater, container, false)
//        networkManager = AppNetworkManager(context)


        return binding.root
    }

}