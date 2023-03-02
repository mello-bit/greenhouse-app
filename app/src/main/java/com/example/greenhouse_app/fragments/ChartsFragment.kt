package com.example.greenhouse_app.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.greenhouse_app.R
import com.example.greenhouse_app.databinding.FragmentChartsBinding


class ChartsFragment : Fragment() {

    private lateinit var binding: FragmentChartsBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentChartsBinding.inflate(inflater, container, false)



        return binding.root
    }


}