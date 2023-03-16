package com.example.greenhouse_app.fragments

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.core.os.ConfigurationCompat
import com.example.greenhouse_app.IntroPage
import com.example.greenhouse_app.MyApplication
import com.example.greenhouse_app.R
import com.example.greenhouse_app.databinding.FragmentSettingsBinding
import com.example.greenhouse_app.utils.AppSettingsManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
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
        val app = requireActivity().applicationContext as MyApplication

        binding.tvCurrentUser.setText(requireContext().getString(R.string.current_user, app.userEmail))
        loadSettings()

        binding.btnSubmit.setOnClickListener {
            saveSettings()
        }

        binding.scLanguage.setOnClickListener {
            val builder = AlertDialog.Builder(requireContext())
            builder.setMessage("Изменения вступят в силу после перезагрузки приложения")
            builder.setPositiveButton("Понятно") {_, _ ->}
            builder.create().show()
        }

        binding.cbEnableAutomationControl.setOnCheckedChangeListener { _, isChecked ->
            setOf(
                binding.etAutomaticWindowOpen,
                binding.etAutomaticHumidifierDisabler,
                binding.etAutomaticSprinkleDisabler
            ).forEach { setEditTextEnabled(it, isChecked) }
        }

        binding.btnLogout.setOnClickListener {
            AppSettingsManager.saveData("cachedUserEmail", "")
            AppSettingsManager.saveData("cachedUserPassword", "")

            val intent = Intent(context, IntroPage::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
        }

        setOf(
            binding.etFurrowOverwetting, binding.etThresholdTemp,
            binding.etGreenhouseOverwetting, binding.etAutomaticSprinkleDisabler,
            binding.etAutomaticWindowOpen, binding.etAutomaticHumidifierDisabler
        ).forEach {it.addTextChangedListener(SettingFilter)}

        return binding.root
    }

    fun setEditTextEnabled(editText: EditText, state: Boolean) {
        editText.isEnabled = state
        editText.setTextColor(if (state) Color.BLACK else Color.WHITE)
        val bg = if (state)
            R.drawable.background_shape_for_boundary else
            R.drawable.background_shape_for_boundary_disabled
        editText.setBackgroundResource(bg)
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
        CoroutineScope(Dispatchers.IO).launch {
            val SavedLanguage = AppSettingsManager.loadData("Language") // "RU", "EN"
            val SavedUnits = AppSettingsManager.loadData("TempUnits")  // "C", "F"
            val SavedInterval = AppSettingsManager.loadData("Interval") // "10", "60", "120"
            val IsEmergencyModeActive =
                AppSettingsManager.loadData("EmergencyMode") == "true" // true, false
            val isAutomationControlActive =
                AppSettingsManager.loadData("AutomationControl") == "true" // true, false

            // Protection params
            val GreenhouseThresholdTemp =
                AppSettingsManager.loadData("GreenhouseThresholdTemp") // 0..100
            val GreenhouseOverwetting =
                AppSettingsManager.loadData("GreenhouseOverwettingPercent") // 0..100
            val FurrowOverwetting = AppSettingsManager.loadData("FurrowOverwettingPercent") // 0.100

            // Automation params
            val automaticallyOpenWindow =
                AppSettingsManager.loadData("automaticallyOpenWindow") ?: "50" // 0..100
            val automaticallyCloseSprinkler =
                AppSettingsManager.loadData("automaticallyCloseSprinkler") ?: "50" // 0..100
            val automaticallyTurnOffHumidifier =
                AppSettingsManager.loadData("automaticallyTurnOffHumidifier") ?: "50" // 0..100


            withContext(Dispatchers.Main) {
                binding.scLanguage.isChecked = SavedLanguage == "EN"
                binding.scTemperatureUnits.isChecked = SavedUnits == "F"
                binding.cbEnableAutomationControl.setChecked(isAutomationControlActive)
                binding.etThresholdTemp.setText(GreenhouseThresholdTemp)
                binding.etFurrowOverwetting.setText(FurrowOverwetting)
                binding.etGreenhouseOverwetting.setText(GreenhouseOverwetting)
                binding.etAutomaticWindowOpen.setText(automaticallyOpenWindow)
                binding.etAutomaticHumidifierDisabler.setText(automaticallyTurnOffHumidifier)
                binding.etAutomaticSprinkleDisabler.setText(automaticallyCloseSprinkler)

                setOf(
                    binding.etAutomaticWindowOpen,
                    binding.etAutomaticHumidifierDisabler,
                    binding.etAutomaticSprinkleDisabler
                ).forEach { setEditTextEnabled(it, isAutomationControlActive) }

                when (SavedInterval) {
                    "10" -> binding.rb10Seconds.isChecked = true
                    "60" -> binding.rb1Minute.isChecked = true
                    "120" -> binding.rb2Minutes.isChecked = true
                }
            }
        }
    }


    fun saveSettings() {
        val language = if (binding.scLanguage.isChecked) "EN" else "RU"
        val tempUnits = if (binding.scTemperatureUnits.isChecked) "F" else "C"
        val interval = when(binding.rgIntervalButtons.checkedRadioButtonId) {
            binding.rb10Seconds.id -> "10"
            binding.rb1Minute.id -> "60"
            binding.rb2Minutes.id -> "120"
            else -> "60"
        }

        val dataKeys = listOf(
            "GreenhouseThresholdTemp", "GreenhouseOverwettingPercent",
            "FurrowOverwettingPercent", "automaticallyOpenWindow",
            "automaticallyCloseSprinkler", "automaticallyTurnOffHumidifier",
            "Language", "TempUnits", "Interval", "AutomationControl"
        )

        val dataValues = listOf(
            binding.etThresholdTemp.text.toString(), binding.etGreenhouseOverwetting.text.toString(),
            binding.etFurrowOverwetting.text.toString(), binding.etAutomaticWindowOpen.text.toString(),
            binding.etAutomaticSprinkleDisabler.text.toString(), binding.etAutomaticHumidifierDisabler.text.toString(),
            language, tempUnits, interval, binding.cbEnableAutomationControl.isChecked.toString()
        )

        dataKeys.zip(dataValues).forEach {
            AppSettingsManager.saveData(it.first, it.second ?: "50")
        }
    }
}