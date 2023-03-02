package com.example.greenhouse_app.fragments

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.FitWindowsLinearLayout
import androidx.core.content.res.ResourcesCompat
import com.example.greenhouse_app.R
import com.example.greenhouse_app.databinding.FragmentHomeBinding
import com.example.greenhouse_app.utils.AppSettingsManager
import com.google.api.Distribution.BucketOptions.Linear
import javax.net.ssl.SSLEngineResult.Status


inline fun <reified T : View> View.findViewWhichIs(): T? {
    val queue = ArrayDeque<View>()
    queue.add(this)
    while (queue.isNotEmpty()) {
        val view = queue.removeFirst()
        if (view is T) {
            return view
        }
        if (view is ViewGroup) {
            for (i in 0 until view.childCount) {
                queue.add(view.getChildAt(i))
            }
        }
    }
    return null
}

class FurrowButton(val Id: Byte, val linearLayout: LinearLayout) {
    var status: Boolean
    var button: AppCompatButton
    var textView: TextView

    fun changeStatus(status: Boolean) {
        this.status = status

        val resource = if (status) R.drawable.green_status_round_button else R.drawable.red_status_round_button
        val text = if (status) R.string.watering_on else R.string.watering_off

        this.textView.setBackgroundResource(resource)
        this.button.setText(text)
    }

    init {
        this.status = AppSettingsManager.loadData("Furrow${this.Id}Status").toBoolean()
        this.button = linearLayout.findViewWhichIs<AppCompatButton>()!!
        this.textView = linearLayout.findViewWhichIs<TextView>()!!
    }

    init {
        changeStatus(this.status)
    }
}


class HomeFragment : Fragment() {
    private lateinit var greenStatus: Drawable.ConstantState
    private lateinit var redStatus: Drawable.ConstantState
    private lateinit var greenStatusBg: Drawable.ConstantState
    private lateinit var redStatusBg: Drawable.ConstantState

    private lateinit var binding: FragmentHomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        greenStatus = ResourcesCompat.getDrawable(resources, R.drawable.green_status_round_button, null)!!.constantState!!
        greenStatusBg = ResourcesCompat.getDrawable(resources, R.drawable.furrow_hydration_on, null)!!.constantState!!
        redStatus = ResourcesCompat.getDrawable(resources, R.drawable.red_status_round_button, null)!!.constantState!!
        redStatusBg = ResourcesCompat.getDrawable(resources, R.drawable.furrow_hydration_off, null)!!.constantState!!
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        binding = FragmentHomeBinding.inflate(inflater, container, false)

        binding.btnWindowStatus.setOnClickListener{
            changeButtonStateBg(it)
            switchText(it as AppCompatButton, resources.getString(R.string.window_closed), resources.getString(R.string.window_open))
        }
        binding.btnHeaterStatus.setOnClickListener{
            changeButtonStateBg(it)
            switchText(it as AppCompatButton, resources.getString(R.string.heater_off), resources.getString(R.string.heater_on))
        }

        val layouts = listOf<LinearLayout>(
            binding.llFurrow1, binding.llFurrow2,
            binding.llFurrow3, binding.llFurrow4,
            binding.llFurrow5, binding.llFurrow6
        )

        val buttonClasses = mutableListOf<FurrowButton>()

        for (i in 1..6) {
            val layout = layouts[i]
            val furrowClass = FurrowButton(i.toByte(), layout)
            layout.setOnClickListener {
                furrowClass.changeStatus(!furrowClass.status)
            }
            buttonClasses.add(furrowClass)
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

        val furrowValues = listOf(
            binding.tvFurrow1Status.text, binding.tvFurrow2Status.text,
            binding.tvFurrow3Status.text, binding.tvFurrow4Status.text,
            binding.tvFurrow5Status.text, binding.tvFurrow6Status.text
        )
        val furrowButtonValues = listOf(
            binding.btnFurrow1.text, binding.btnFurrow2.text,
            binding.btnFurrow3.text, binding.btnFurrow4.text,
            binding.btnFurrow5.text, binding.btnFurrow6.text
        )

        for (i in 1..6) {
            AppSettingsManager.saveData("tvFurrow${i}Status", "$i - ${furrowValues[i - 1]}")
            AppSettingsManager.saveData("btnFurrow$i", "$i - ${furrowButtonValues[i - 1]}")
        }
        AppSettingsManager.saveData("btnWindowStatus", binding.btnWindowStatus.text.toString())
        AppSettingsManager.saveData("btnHeaterStatus", binding.btnHeaterStatus.text.toString())
        Log.d("SaveTag", "Data has saved")

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