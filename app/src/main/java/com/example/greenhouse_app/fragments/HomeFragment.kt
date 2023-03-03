package com.example.greenhouse_app.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import com.example.greenhouse_app.MyApplication
import com.example.greenhouse_app.R
import com.example.greenhouse_app.dataClasses.SoilHum
import com.example.greenhouse_app.dataClasses.TempAndHum
import com.example.greenhouse_app.databinding.FragmentHomeBinding
import com.example.greenhouse_app.utils.AppSettingsManager


interface ApiListener {
    fun onApiResponseReceived(response: Pair<MutableSet<SoilHum>, MutableSet<TempAndHum>>)
}

class FurrowButton(val Id: Byte, val linearLayout: LinearLayout, val BottomButton: Boolean = true) {
    var status: Boolean
    var button: AppCompatButton
    var textView: TextView

    fun changeStatus(status: Boolean) {
        this.status = status

        val btnBackground = if (status) R.drawable.green_status_round_button else R.drawable.red_status_round_button
        val llBackground = if (status) R.drawable.furrow_hydration_on else R.drawable.furrow_hydration_off
        val text = if (status) R.string.watering_on else R.string.watering_off

        this.linearLayout.setBackgroundResource(llBackground)
        this.button.setBackgroundResource(btnBackground)
        this.button.setText(text)
    }

    fun changeDisplayValue(value: Byte) {
        this.textView.text = "$value%"
    }

    init {
        this.status = AppSettingsManager.loadData("Furrow${this.Id}Status").toBoolean()
        this.button = linearLayout.findViewWithTag<AppCompatButton>("btnFurrow$Id")
        this.button.setOnClickListener{ changeStatus(!this.status) }
        this.textView = linearLayout.findViewWithTag<TextView>("tvFurrow${Id}Status")

        Log.d(null, this.button.tag.toString())
        Log.d(null, this.textView.tag.toString())
    }

    init {
        val status = AppSettingsManager.loadData("Furrow${this.Id}Status")

        if (status != null && status.isNotEmpty()) {
            changeStatus(status.toBoolean())
            changeDisplayValue((1..100).random().toByte())
        } else {
            Log.d(null,"THE RECEIVED STATUS IS $status")
        }
    }
}


class HomeFragment : Fragment(), ApiListener {
    private lateinit var binding: FragmentHomeBinding
    private var buttonClasses = mutableMapOf<Byte, FurrowButton>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity?.application as? MyApplication)?.setApiListener(this)
    }

    override fun onApiResponseReceived(response: Pair<MutableSet<SoilHum>, MutableSet<TempAndHum>>) {
        if (isAdded and buttonClasses.isNotEmpty()) {

            for (soilHumidity in response.first) {
                buttonClasses[soilHumidity.id]?.changeDisplayValue(soilHumidity.humidity.toByte())
            }

        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        binding = FragmentHomeBinding.inflate(inflater, container, false)

        binding.btnWindowStatus.setOnClickListener{

        }
        binding.btnHeaterStatus.setOnClickListener{

        }

        val layouts = listOf<LinearLayout>(
            binding.llFurrow1, binding.llFurrow2,
            binding.llFurrow3, binding.llFurrow4,
            binding.llFurrow5, binding.llFurrow6
        )


        for (i in 1..6) {
            val layout = layouts[i - 1]
            val furrowClass = FurrowButton(i.toByte(), layout)
            layout.setOnClickListener {
                furrowClass.changeStatus(!furrowClass.status)
            }
            this.buttonClasses[i.toByte()] = furrowClass
        }

        return binding.root
    }


    override fun onResume() {
        super.onResume()

        // reading data from shared preferences
        val furrowValues = mutableListOf<String>()
        val furrowButtonsValues = mutableListOf<String>()
        val bottomButtonsStatus = listOf(
            AppSettingsManager.loadData("btnWindowStatus"),
            AppSettingsManager.loadData("btnHeaterStatus")
        )
        for (i in 1..6) {
            val furrowValue = AppSettingsManager.loadData("tvFurrow${i}Status")
            val furrowButtonValue = AppSettingsManager.loadData("btnFurrow$i")

            if (furrowValue.toString().isNotEmpty())
                furrowValues.add(furrowValue.toString())

            if (furrowButtonValue.toString().isNotEmpty())
                furrowButtonsValues.add(furrowButtonValue.toString())
        }

        Log.d("SaveTag", furrowValues.toString())
        Log.d("SaveTag", furrowButtonsValues.toString())
        Log.d("SaveTag", bottomButtonsStatus.toString())
    }

    override fun onStop() {
        super.onStop()

        this.buttonClasses.forEach {
            val key = "Furrow${it.value.Id}Status"
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