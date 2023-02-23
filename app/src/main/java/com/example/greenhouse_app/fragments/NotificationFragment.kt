package com.example.greenhouse_app.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.NotificationCompat
import com.example.greenhouse_app.databinding.FragmentNotificationBinding
import com.example.greenhouse_app.utils.AppNotificationManager
import com.example.greenhouse_app.utils.AppSettingsManager


class NotificationFragment : Fragment() {

    private lateinit var binding: FragmentNotificationBinding
    private lateinit var builder: NotificationCompat

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

            AppNotificationManager.showNotification()
        }

        return binding.root
    }
}