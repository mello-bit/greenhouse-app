package com.example.greenhouse_app.fragments

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.greenhouse_app.R
import com.example.greenhouse_app.dataClasses.ListForData
import com.example.greenhouse_app.databinding.FragmentChartsBinding
import com.example.greenhouse_app.recyclerView.DataAdapter
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import java.text.SimpleDateFormat
import java.util.*


class ChartsFragment : Fragment() {

    private lateinit var binding: FragmentChartsBinding
    private lateinit var recyclerView: RecyclerView
    private lateinit var handler: Handler
    private val myAdapter by lazy { DataAdapter() }

    private var hour: Int? = null
    private var date: String? = null

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentChartsBinding.inflate(inflater, container, false)
        handler = Handler(Looper.getMainLooper())

        binding.ibChartButton.setOnClickListener {
            val chartFragment = ChartFragment()
            replaceFragment(chartFragment)
        }

        binding.ibTableButton.setOnClickListener {
            val chartFragment = ChartFragment()
            replaceFragment(chartFragment)
        }

        binding.chooseTheDate.setOnClickListener {
            showDatePickerDialog()
        }

        binding.chooseTheTime.setOnClickListener {
            showTimePickerDialog()
        }

        val layoutManager = LinearLayoutManager(context)

        recyclerView = binding.rvDataFromSensors
        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = myAdapter

        myAdapter.setData(ListForData.EverySoilHumDataList)
        myAdapter.notifyDataSetChanged()

        handler.post(object : Runnable {
            override fun run() {
                myAdapter.setData(ListForData.EverySoilHumDataList)
                myAdapter.notifyDataSetChanged()
                if (!recyclerView.canScrollVertically(1)) {
                    recyclerView.scrollToPosition(ListForData.EverySoilHumDataList.size - 1)
                }
                handler.postDelayed(this, 200)
            }
        })

        return binding.root
    }

    private fun showTimePickerDialog() {
        val picker = MaterialTimePicker.Builder()
            .setTimeFormat(TimeFormat.CLOCK_24H)
            .setMinute(0)
            .build()
        picker.show(childFragmentManager, "TAG")
        picker.addOnPositiveButtonClickListener {
            hour = picker.hour
            Log.d("CheckDateTime", hour.toString())
        }
    }

    private fun showDatePickerDialog() {
        val myCalendar = Calendar.getInstance()
        val datePicker = DatePickerDialog.OnDateSetListener { _, year, month, day ->
            myCalendar.set(Calendar.YEAR, year)
            myCalendar.set(Calendar.MONTH, month)
            myCalendar.set(Calendar.DAY_OF_MONTH, day)
            formatterDate(myCalendar)
        }

        DatePickerDialog(requireContext(), datePicker,
            myCalendar.get(Calendar.YEAR),
            myCalendar.get(Calendar.MONTH),
            myCalendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    private fun formatterDate(calendar: Calendar) {
        val myFormat = "yyyy-mm-dd"
        val sdf = SimpleDateFormat(myFormat, Locale.UK)
        date = sdf.format(calendar.time)

        Log.d("CheckDateTime", date ?: "0")
    }

    private fun replaceFragment(fragment: Fragment) {
        val fragmentManager = childFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.charts_fragment, fragment)
        fragmentTransaction.commit()
    }

}