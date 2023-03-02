package com.example.greenhouse_app.fragments

import android.content.Context
import android.content.res.Resources
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.FitWindowsLinearLayout
import androidx.core.content.res.ResourcesCompat
import com.example.greenhouse_app.R
import com.example.greenhouse_app.databinding.FragmentHomeBinding
import com.example.greenhouse_app.utils.AppSettingsManager
import com.google.api.Distribution.BucketOptions.Linear
import org.checkerframework.common.subtyping.qual.Bottom
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

class FurrowButton(val Id: Byte, val linearLayout: LinearLayout, val BottomButton: Boolean = true) {
    var status: Boolean
    var button: AppCompatButton
    var textView: TextView

    fun changeStatus(status: Boolean) {
        this.status = status

        val btn_background = if (status) R.drawable.green_status_round_button else R.drawable.red_status_round_button
        val ll_background = if (status) R.drawable.furrow_hydration_on else R.drawable.furrow_hydration_off
        val text = if (status) R.string.watering_on else R.string.watering_off

        this.linearLayout.setBackgroundResource(ll_background)
        this.button.setBackgroundResource(btn_background)
        this.button.setText(text)
    }

    init {
        this.status = AppSettingsManager.loadData("Furrow${this.Id}Status").toBoolean()
        this.button = linearLayout.findViewWithTag<AppCompatButton>("btnFurrow$Id")
        this.textView = linearLayout.findViewWithTag<TextView>("tvFurrow${Id}Status")

        Log.d(null, this.button.tag.toString())
        Log.d(null, this.textView.tag.toString())
    }

    init {
        val status = AppSettingsManager.loadData("Furrow${this.Id}Status")

        if (status != null && status.isNotEmpty()) {
            changeStatus(status.toBoolean())
        } else {
            Log.d(null,"THE RECEIVED STATUS IS $status")
        }
    }
}


class HomeFragment : Fragment() {
    private lateinit var binding: FragmentHomeBinding
    private var buttonClasses = mutableListOf<FurrowButton>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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
            this.buttonClasses.add(furrowClass)
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
            val key = "Furrow${it.Id}Status"
            AppSettingsManager.saveData(key, it.status.toString())
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