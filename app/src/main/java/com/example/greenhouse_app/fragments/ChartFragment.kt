package com.example.greenhouse_app.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.greenhouse_app.R
import com.example.greenhouse_app.databinding.FragmentChartBinding
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet


class ChartFragment : Fragment() {

    private lateinit var binding: FragmentChartBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentChartBinding.inflate(inflater, container, false)

        binding.ibTableButton.setOnClickListener {
            val tableFragment = ChartsFragment()
            replaceFragment(tableFragment)
        }

        val someEntries = mutableListOf<Entry>()
//        someEntries.add(Entry(0f, 5f))
//        someEntries.add(Entry(1f, 4f))
//        someEntries.add(Entry(2f, 7f))
//        someEntries.add(Entry(3f, 2f))
//        someEntries.add(Entry(4f, 9f))
//        someEntries.add(Entry(5f, 6f))

        val _lineDataSet= MutableLiveData(LineDataSet(someEntries, "Легенда"))
        someEntries.add(Entry(0f, 5f))
        someEntries.add(Entry(1f, 4f))
        someEntries.add(Entry(2f, 7f))
        someEntries.add(Entry(3f, 2f))
        someEntries.add(Entry(4f, 9f))
        someEntries.add(Entry(5f, 6f))
        _lineDataSet.value = LineDataSet(someEntries, "Легенда")
        val lineDataSet: LiveData<LineDataSet> = _lineDataSet
        val lineData = LineData(_lineDataSet.value)
        binding.lcLineChart.data = lineData


        return binding.root
    }

    private fun replaceFragment(fragment: Fragment) {
        val fragmentManager = childFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.chart_fragment, fragment)
        fragmentTransaction.commit()
    }
}