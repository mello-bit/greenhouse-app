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


class HomeFragment : Fragment() {
    private lateinit var greenStatus: Drawable.ConstantState
    private lateinit var redStatus: Drawable.ConstantState

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        greenStatus = ResourcesCompat.getDrawable(resources, R.drawable.green_status_round_button, null)!!.constantState!!
        redStatus = ResourcesCompat.getDrawable(resources, R.drawable.red_status_round_button, null)!!.constantState!!


    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val binding = FragmentHomeBinding.inflate(inflater, container, false)

        binding.btnWindowStatus.setOnClickListener{
            changeBackgroundStatus(it)
            switchText(it as AppCompatButton, resources.getString(R.string.window_closed), resources.getString(R.string.window_open))
        }
        binding.btnHeaterStatus.setOnClickListener{
            changeBackgroundStatus(it)
            switchText(it as AppCompatButton, resources.getString(R.string.heater_off), resources.getString(R.string.heater_on))
        }

        return binding.root
    }

    private fun switchText(btn: AppCompatButton, text1: String, text2: String) {
        when (btn.text) {
            text1 -> btn.text = text2
            text2 -> btn.text = text1
        }
    }
    private fun changeBackgroundStatus(btn: View) {
        when (btn.background.constantState) {
            greenStatus -> btn.setBackgroundResource(R.drawable.red_status_round_button)
            redStatus -> btn.setBackgroundResource(R.drawable.green_status_round_button)
            else -> Log.e("UI", "Drawable error occured")
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