package com.example.greenhouse_app.fragments

import android.content.Context
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.ConfigurationCompat
import com.example.greenhouse_app.databinding.FragmentSettingsBinding
import com.example.greenhouse_app.utils.AppSettingsManager
import java.util.*

val SettingFilter = object : TextWatcher {
    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

    override fun afterTextChanged(s: Editable?) {
        val input = s?.toString()?.toIntOrNull()

        if (s != null && s.isNotEmpty()) {
            if (s.toString() == "00" || input.toString() != s.toString()) {
                s.replace(0, s.length, "0")
            } else if(input == null || input !in 0..100) {
                val nearestValidInput = input?.coerceIn(0, 100) ?: 0
                s.replace(0, s.length, nearestValidInput.toString())
            }
        }
    }
}


open class SettingsFragment : Fragment() {
    private lateinit var binding: FragmentSettingsBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSettingsBinding.inflate(inflater, container, false)
        loadSettings()

        binding.btnSubmit.setOnClickListener {
            saveSettings()
        }

        binding.scLanguage.setOnCheckedChangeListener { _, isChecked ->
            val language = if (isChecked) "EN" else "RU"
            setLocale(language)
        }

        binding.etFurrowOverwetting.addTextChangedListener(SettingFilter)
        binding.etThresholdTemp.addTextChangedListener(SettingFilter)
        binding.etGreenhouseOverwetting.addTextChangedListener(SettingFilter)

        return binding.root
    }

    override fun onPause() {
        super.onPause()
        loadSettings()
    }

    fun setLocale(languageCode: String) {
        Log.d("important", "Accessed with: $languageCode")
        val locale = Locale("en")
        val configuration = resources.configuration
        configuration.setLocale(locale)
        val context = requireContext().createConfigurationContext(configuration)
        requireActivity().applicationContext.resources.updateConfiguration(configuration, requireActivity().resources.displayMetrics)
        requireActivity().recreate()
    }

    fun loadSettings() {
        val SavedLanguage = AppSettingsManager.loadData("Language") // "RU", "EN"
        val SavedUnits = AppSettingsManager.loadData("TempUnits")  // "C", "F"
        val SavedInterval = AppSettingsManager.loadData("Interval") // "10", "60", "120"
        val IsEmergencyModeActive = AppSettingsManager.loadData("EmergencyMode") == "true" // true, false
        val GreenhouseThresholdTemp = AppSettingsManager.loadData("GreenhouseThresholdTemp") // 0..100
        val GreenhouseOverwetting = AppSettingsManager.loadData("GreenhouseOverwettingPercent") // 0..100
        val FurrowOverwetting = AppSettingsManager.loadData("FurrowOverwettingPercent") // 0.100

        binding.scLanguage.isChecked = SavedLanguage == "EN"
        binding.scTemperatureUnits.isChecked = SavedUnits == "F"
        binding.etThresholdTemp.setText(GreenhouseThresholdTemp)
        binding.etFurrowOverwetting.setText(FurrowOverwetting)
        binding.etGreenhouseOverwetting.setText(GreenhouseOverwetting)

        when (SavedInterval) {
            "10" -> binding.rb10Seconds.isChecked = true
            "60" -> binding.rb1Minute.isChecked = true
            "120" -> binding.rb2Minutes.isChecked = true
        }
    }

    fun saveSettings() {
        val language = if (binding.scLanguage.isChecked) "EN" else "RU"
        val tempUnits = if (binding.scTemperatureUnits.isChecked) "F" else "C"
        val Interval = when(binding.rgIntervalButtons.checkedRadioButtonId) {
            binding.rb10Seconds.id -> "10"
            binding.rb1Minute.id -> "60"
            binding.rb2Minutes.id -> "120"
            else -> "60"
        }

        AppSettingsManager.saveData("GreenhouseThresholdTemp", binding.etThresholdTemp.text.toString())
        AppSettingsManager.saveData("GreenhouseOverwettingPercent", binding.etGreenhouseOverwetting.text.toString())
        AppSettingsManager.saveData("FurrowOverwettingPercent", binding.etFurrowOverwetting.text.toString())
        AppSettingsManager.saveData("Language", language)
        AppSettingsManager.saveData("TempUnits", tempUnits)
        AppSettingsManager.saveData("Interval", Interval)
    }
}