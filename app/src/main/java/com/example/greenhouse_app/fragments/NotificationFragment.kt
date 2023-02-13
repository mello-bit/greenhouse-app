package com.example.greenhouse_app.fragments

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.greenhouse_app.R
import com.example.greenhouse_app.databinding.FragmentNotificationBinding
import com.example.greenhouse_app.databinding.FragmentSettingsBinding


class NotificationFragment : Fragment() {
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var binding: FragmentNotificationBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedPreferences = requireActivity().getPreferences(Context.MODE_PRIVATE)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentNotificationBinding.inflate(inflater, container, false)

        binding.loadDtNt.setOnClickListener {
            val tm = loadData("tempV")
            val hm = loadData("humV")
            println("$tm $hm")
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