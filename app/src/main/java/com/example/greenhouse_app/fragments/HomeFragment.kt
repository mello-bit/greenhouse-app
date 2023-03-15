package com.example.greenhouse_app.fragments

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton
import com.example.greenhouse_app.MainActivity
import com.example.greenhouse_app.MyApplication
import com.example.greenhouse_app.R
import com.example.greenhouse_app.dataClasses.SoilHum
import com.example.greenhouse_app.dataClasses.TempAndHum
import com.example.greenhouse_app.databinding.FragmentHomeBinding
import com.example.greenhouse_app.utils.AppDatabase
import com.example.greenhouse_app.utils.AppDatabaseHelper
import com.example.greenhouse_app.utils.AppNetworkManager
import com.example.greenhouse_app.utils.AppSettingsManager
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.checkerframework.checker.units.qual.Temperature
import org.w3c.dom.Text
import kotlin.math.roundToInt
import kotlin.properties.Delegates


interface ApiListener {
    fun onApiResponseReceived(response: Pair<MutableList<SoilHum>, MutableList<TempAndHum>>)
}

enum class STATE { OFF, ON, DISABLED;
    fun toggle(): STATE {
        return when(this) {
            ON -> OFF
            OFF -> ON
            DISABLED -> this
        }
    }
}

class FurrowButton(val Id: Byte, val linearLayout: LinearLayout, private val networkManager: AppNetworkManager, private val context: Context) {
    var status: STATE
    private var button: AppCompatButton
    private var textView: TextView
    private var patchFunction = networkManager::changeFurrowState
    private var protectionValue: Byte = 65

    fun changeStatus(status: STATE, send_patch: Boolean = false) {
        this.status = status

        val btnBackground = when(status) {
            STATE.ON -> R.drawable.green_status_round_button
            STATE.OFF -> R.drawable.red_status_round_button
            STATE.DISABLED -> R.drawable.gray_status_round_button
        }

        val llBackground = when(status) {
            STATE.ON -> R.drawable.furrow_hydration_on
            STATE.OFF -> R.drawable.furrow_hydration_off
            STATE.DISABLED -> R.drawable.furrow_hydration_disabled
        }

        val text = when(status) {
            STATE.ON -> R.string.watering_on
            else -> R.string.watering_off
        }

        if (send_patch) {
            val patchStatus = if (status == STATE.DISABLED) 0 else status.ordinal.toByte()
            patchFunction(Id, patchStatus)
        }
        this.linearLayout.setBackgroundResource(llBackground)
        this.button.setBackgroundResource(btnBackground)
        this.button.setText(text)
    }

    fun changeDisplayValue(value: Byte) {
        this.textView.text = "$value%"

        if (value > protectionValue) {
            this.changeStatus(STATE.DISABLED, this.status == STATE.ON)
        } else if(value <= protectionValue && this.status == STATE.DISABLED) {
            this.changeStatus(STATE.OFF, false)
        }
    }

    init {
        this.status = STATE.valueOf(AppSettingsManager.loadData("Furrow${this.Id}Status") ?: "OFF")
        this.button = linearLayout.findViewWithTag<AppCompatButton>("btnFurrow$Id")
        this.textView = linearLayout.findViewWithTag<TextView>("tvFurrow${Id}Status")
        this.button.setOnClickListener{
            if (this.status != STATE.DISABLED) {
                changeStatus(this.status.toggle(), true)
            } else {
                MainActivity.showToast(context, context.getString(R.string.furrow_disabled_message), true)
            }
        }
    }

    init {
        val status = STATE.valueOf(AppSettingsManager.loadData("Furrow${this.Id}Status") ?: "OFF")
        changeStatus(status, false)
    }
}

// Names: <Window, Humidifier>
class BottomHomeButton(val name: String, val linearLayout: LinearLayout, val textView: TextView, private val networkManager: AppNetworkManager) {
    var status: Boolean = false
    private val turnOnText: Int = if (name == "Humidifier") R.string.humidifier_on else R.string.window_open
    private val turnOffText: Int = if (name == "Humidifier") R.string.humidifier_off else R.string.window_closed
    private lateinit var patchFunction: (Byte) -> Unit

    init {
        patchFunction = if (name == "Window") {
            networkManager::changeWindowState
        } else {
            networkManager::changeGlobalWateringState
        }
        changeStatus(AppSettingsManager.loadData("BottomButton$name").toBoolean(), false)
    }

    fun changeStatus(status: Boolean, send_patch: Boolean) {
        this.status = status

        val bgResource = if (status) R.drawable.green_status_round_button else R.drawable.red_status_round_button
        val newText = if (status) turnOnText else turnOffText

        if (send_patch) {
            patchFunction(status.compareTo(false).toByte())
        }
        linearLayout.setBackgroundResource(bgResource)
        textView.setText(newText)
    }
}

class HomeFragment : Fragment(), ApiListener {
    private lateinit var binding: FragmentHomeBinding
    private var buttonClasses = mutableMapOf<Byte, FurrowButton>()
    private var bottomButtons = mutableMapOf<String, BottomHomeButton>()
    private lateinit var db: AppDatabase
    private lateinit var application: MyApplication
    private lateinit var networkManager: AppNetworkManager
    private var TemperatureUnits = R.string.temperature_celsius
    private var ThresholdTemperature = 100
    private var FurrowOverhydration = 100
    private var GreenhouseOverhydration = 100

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        TemperatureUnits =
            if (AppSettingsManager.loadData("TempUnits") == "C") R.string.temperature_celsius else R.string.fahrenheit

        val applicationContext = requireActivity().applicationContext
        application = applicationContext as MyApplication
        db = AppDatabaseHelper.getDatabase(applicationContext, application.currentUID)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        if (!::networkManager.isInitialized) {
            networkManager = AppNetworkManager(context.applicationContext)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity?.application as? MyApplication)?.setApiListener(this)
    }

    override fun onApiResponseReceived(response: Pair<MutableList<SoilHum>, MutableList<TempAndHum>>) {
        if (isAdded and buttonClasses.isNotEmpty()) {

            for (soilHumidity in response.first) {
                buttonClasses[soilHumidity.id]?.changeDisplayValue(soilHumidity.humidity.toByte())
            }

            var generalGreenhouseHumidity = 0f
            var generalGreenhouseTemperature = 0f
            for (tempHumidity in response.second) {
                generalGreenhouseHumidity += tempHumidity.hum.toFloat()
                generalGreenhouseTemperature += tempHumidity.temp.toFloat()
            }

            binding.tvGreenhouseHumidity.text = getString(
                R.string.percent_adder,
                "%.1f".format(generalGreenhouseHumidity / 4)
            )

            var temp = generalGreenhouseTemperature / 4
            if (TemperatureUnits == R.string.temperature_fahrenheit) {
                temp = (temp * 1.8 + 32).toFloat()
            }

            binding.tvGreenhouseTemp.text = getString(
                TemperatureUnits,
                "%.1f".format(temp)
            )
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        binding = FragmentHomeBinding.inflate(inflater, container, false)

        val layouts = listOf<LinearLayout>(
            binding.llFurrow1, binding.llFurrow2,
            binding.llFurrow3, binding.llFurrow4,
            binding.llFurrow5, binding.llFurrow6
        )

        mapOf<String, Pair<LinearLayout, TextView>>(
            "Window" to Pair(binding.llWindowContainer, binding.tvDisplayState1),
            "Humidifier" to Pair(binding.llHumidifierContainer, binding.tvDisplayState2)
        ).forEach {
            val newInstance = BottomHomeButton(it.key, it.value.first, it.value.second, networkManager)

            newInstance.linearLayout.setOnClickListener {
                newInstance.changeStatus(!newInstance.status, true)
            }

            this.bottomButtons[it.key] = newInstance
        }

        for (i in 1..6) {
            val layout = layouts[i - 1]
            val furrowClass = FurrowButton(i.toByte(), layout, networkManager, this.requireContext())
            layout.setOnClickListener {
                if (furrowClass.status != STATE.DISABLED) {
                    furrowClass.changeStatus(furrowClass.status.toggle(), true)
                } else {
                    MainActivity.showToast(requireContext(), requireContext().getString(R.string.furrow_disabled_message), true)
                }
            }
            this.buttonClasses[i.toByte()] = furrowClass
        }

        return binding.root
    }


    override fun onResume() {
        super.onResume()
        TemperatureUnits =
            if (AppSettingsManager.loadData("TempUnits") == "C") R.string.temperature_celsius else R.string.temperature_fahrenheit

        // Восстановление отображаемых данных после выхода/захода на фрагмент
        CoroutineScope(Dispatchers.IO).launch {
            val latestData = db.SensorDao().getLatestSensorData()
            if (latestData != null) {
                var temp = latestData.greenhouse_temperature
                if (TemperatureUnits == R.string.temperature_fahrenheit) {
                    temp = (temp * 1.8 + 32).toFloat()
                }

                binding.tvGreenhouseTemp.text = getString(
                    TemperatureUnits,
                    String.format("%.1f", temp)
                )
                binding.tvGreenhouseHumidity.text = getString(
                    R.string.percent_adder,
                    String.format("%.1f", latestData.greenhouse_humidity)
                )

                listOf(
                    latestData.furrow1_humidity, latestData.furrow2_humidity,
                    latestData.furrow3_humidity, latestData.furrow4_humidity,
                    latestData.furrow5_humidity, latestData.furrow6_humidity
                ).forEachIndexed { index, fl ->
                    buttonClasses[(index + 1).toByte()]?.changeDisplayValue(fl.toInt().toByte())
                }
            }
        }
    }

    override fun onStop() {
        super.onStop()

        this.buttonClasses.forEach {
            val key = "Furrow${it.value.Id}Status"
            AppSettingsManager.saveData(key, it.value.status.toString())
        }

        this.bottomButtons.forEach {
            val key = "BottomButton${it.value.name}"
            AppSettingsManager.saveData(key, it.value.status.toString())
        }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment HomeFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            HomeFragment().apply {
                arguments = Bundle().apply {

                }
            }
    }
}