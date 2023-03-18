package com.example.greenhouse_app.fragments

import android.content.Intent
import android.graphics.Color
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
import androidx.appcompat.widget.AppCompatButton
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
    private lateinit var application: MyApplication
    private var interval = AppSettingsManager.loadData("Interval")?.toInt() ?: 60

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        application = requireActivity().applicationContext as MyApplication
    }

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
            builder.setMessage(R.string.settings_changed_after_restart_message)
            builder.setPositiveButton(R.string.agreed) {_, _ ->}
            builder.create().show()
        }

        binding.btnEmergency.setOnClickListener{
            if (binding.btnEmergency.text.toString() == getString(R.string.extreme_mode_off)) {
                val builder = AlertDialog.Builder(requireContext())
                builder.setMessage(R.string.extreme_mode_warning_message)
                builder.setPositiveButton(R.string.yes) { _, _ ->
                    AppSettingsManager.saveData("EmergencyMode", "true")
                    it.setBackgroundResource(R.drawable.red_status_round_button)
                    (it as AppCompatButton).setText(R.string.extreme_mode_on)
                }
                builder.setNegativeButton(R.string.cancel) { _, _ -> }
                builder.create().show()
            } else {
                AppSettingsManager.saveData("EmergencyMode", "false")
                it.setBackgroundResource(R.drawable.green_status_round_button)
                (it as AppCompatButton).setText(R.string.extreme_mode_off)
            }
        }

        binding.cbEnableAutomationControl.setOnCheckedChangeListener { _, isChecked ->
            setOf(
                binding.etAutomaticWindowOpen,
                binding.etAutomaticHumidifierEnabler,
                binding.etAutomaticSprinkleEnabler
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
            binding.etGreenhouseOverwetting, binding.etAutomaticSprinkleEnabler,
            binding.etAutomaticWindowOpen, binding.etAutomaticHumidifierEnabler
        ).forEach {it.addTextChangedListener(SettingFilter)}

        return binding.root
    }

    private fun setEditTextEnabled(editText: EditText, state: Boolean) {
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

    private fun loadSettings() {
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
                AppSettingsManager.loadData("GreenhouseThresholdTemp") ?: "0" // 0..100
            val GreenhouseOverwetting =
                AppSettingsManager.loadData("GreenhouseOverwettingPercent") ?: "100" // 0..100
            val FurrowOverwetting = AppSettingsManager.loadData("FurrowOverwettingPercent") ?: "100" // 0.100

            // Automation params
            val automaticallyOpenWindow =
                AppSettingsManager.loadData("automaticallyOpenWindow") ?: "100" // 0..100
            val automaticallyOpenSprinkler =
                AppSettingsManager.loadData("automaticallyOpenSprinkler") ?: "100" // 0..100
            val automaticallyTurnOnHumidifier =
                AppSettingsManager.loadData("automaticallyTurnOnHumidifier") ?: "100" // 0..100


            withContext(Dispatchers.Main) {
                binding.scLanguage.isChecked = SavedLanguage == "EN"
                binding.scTemperatureUnits.isChecked = SavedUnits == "F"
                binding.cbEnableAutomationControl.setChecked(isAutomationControlActive)
                binding.etThresholdTemp.setText(GreenhouseThresholdTemp)
                binding.etFurrowOverwetting.setText(FurrowOverwetting)
                binding.etGreenhouseOverwetting.setText(GreenhouseOverwetting)
                binding.etAutomaticWindowOpen.setText(automaticallyOpenWindow)
                binding.etAutomaticHumidifierEnabler.setText(automaticallyTurnOnHumidifier)
                binding.etAutomaticSprinkleEnabler.setText(automaticallyOpenSprinkler)

                setOf(
                    binding.etAutomaticWindowOpen,
                    binding.etAutomaticHumidifierEnabler,
                    binding.etAutomaticSprinkleEnabler
                ).forEach { setEditTextEnabled(it, isAutomationControlActive) }

                if (IsEmergencyModeActive) {
                    binding.btnEmergency.setBackgroundResource(R.drawable.red_status_round_button)
                    binding.btnEmergency.setText(R.string.extreme_mode_on)
                }

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
        val newInterval = when(binding.rgIntervalButtons.checkedRadioButtonId) {
            binding.rb10Seconds.id -> "10"
            binding.rb1Minute.id -> "60"
            binding.rb2Minutes.id -> "120"
            else -> "60"
        }

        if (newInterval.toInt() != interval) {
            interval = newInterval.toInt()
            application.createNewDataflow(newInterval.toByte())
            application.interval = newInterval.toInt()
        }


        val dataKeys = listOf(
            "GreenhouseThresholdTemp", "GreenhouseOverwettingPercent",
            "FurrowOverwettingPercent", "automaticallyOpenWindow",
            "automaticallyOpenSprinkler", "automaticallyTurnOnHumidifier",
            "Language", "TempUnits", "Interval", "AutomationControl"
        )

        val dataValues = listOf(
            binding.etThresholdTemp.text.toString(), binding.etGreenhouseOverwetting.text.toString(),
            binding.etFurrowOverwetting.text.toString(), binding.etAutomaticWindowOpen.text.toString(),
            binding.etAutomaticSprinkleEnabler.text.toString(), binding.etAutomaticHumidifierEnabler.text.toString(),
            language, tempUnits, newInterval, binding.cbEnableAutomationControl.isChecked.toString()
        )

        dataKeys.zip(dataValues).forEach {
            AppSettingsManager.saveData(it.first, it.second ?: "50")
        }
    }
}