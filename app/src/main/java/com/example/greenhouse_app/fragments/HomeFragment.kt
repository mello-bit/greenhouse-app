package com.example.greenhouse_app.fragments

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.res.ResourcesCompat
import com.example.greenhouse_app.R
import com.example.greenhouse_app.databinding.FragmentHomeBinding


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


class HomeFragment : Fragment() {
    private lateinit var greenStatus: Drawable.ConstantState
    private lateinit var redStatus: Drawable.ConstantState
    private lateinit var greenStatusBg: Drawable.ConstantState
    private lateinit var redStatusBg: Drawable.ConstantState

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        greenStatus = ResourcesCompat.getDrawable(resources, R.drawable.green_status_round_button, null)!!.constantState!!
        greenStatusBg = ResourcesCompat.getDrawable(resources, R.drawable.furrow_hydration_on, null)!!.constantState!!
        redStatus = ResourcesCompat.getDrawable(resources, R.drawable.red_status_round_button, null)!!.constantState!!
        redStatusBg = ResourcesCompat.getDrawable(resources, R.drawable.furrow_hydration_off, null)!!.constantState!!
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val binding = FragmentHomeBinding.inflate(inflater, container, false)

        binding.btnWindowStatus.setOnClickListener{
            changeButtonStateBg(it)
            switchText(it as AppCompatButton, resources.getString(R.string.window_closed), resources.getString(R.string.window_open))
        }
        binding.btnHeaterStatus.setOnClickListener{
            changeButtonStateBg(it)
            switchText(it as AppCompatButton, resources.getString(R.string.heater_off), resources.getString(R.string.heater_on))
        }

        val furrowButtons = setOf(
            binding.llFurrow1, binding.llFurrow2,
            binding.llFurrow3, binding.llFurrow4,
            binding.llFurrow5, binding.llFurrow6
        )

        furrowButtons.forEach {layout ->
            layout.setOnClickListener {
                val button = it.findViewWhichIs<AppCompatButton>()
                changeButtonStateBg(button as View)
                changeFurrowStateBg(layout as View)
                switchText(button as AppCompatButton, resources.getString(R.string.watering_on), resources.getString(R.string.watering_off))
            }
        }

        return binding.root
    }

    private fun switchText(btn: AppCompatButton, text1: String, text2: String) {
        when (btn.text) {
            text1 -> btn.text = text2
            text2 -> btn.text = text1
        }
    }
    private fun changeButtonStateBg(btn: View) {
        when (btn.background.constantState) {
            greenStatus -> btn.setBackgroundResource(R.drawable.red_status_round_button)
            redStatus -> btn.setBackgroundResource(R.drawable.green_status_round_button)
            else -> Log.e("UI", "Drawable error occured")
        }
    }

    private fun changeFurrowStateBg(ll: View) {
        when (ll.background.constantState) {
            greenStatusBg -> ll.setBackgroundResource(R.drawable.furrow_hydration_off)
            redStatusBg -> ll.setBackgroundResource(R.drawable.furrow_hydration_on)
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