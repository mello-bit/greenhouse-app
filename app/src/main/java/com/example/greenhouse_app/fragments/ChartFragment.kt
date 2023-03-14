package com.example.greenhouse_app.fragments

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.example.greenhouse_app.R
import com.example.greenhouse_app.dataClasses.ListForData
import com.example.greenhouse_app.databinding.FragmentChartBinding
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.formatter.ValueFormatter
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


class ChartFragment : Fragment() {

    private lateinit var binding: FragmentChartBinding
    private lateinit var handler: Handler

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentChartBinding.inflate(inflater, container, false)
        handler = Handler(Looper.getMainLooper())


        binding.ibTableButton.setOnClickListener {
            val tableFragment = ChartsFragment()
            replaceFragment(tableFragment)
        }

        handler.post(object : Runnable {
            override fun run() {
                fromAllDataToEntry()
                handler.postDelayed(this, 500)
            }
        })


        return binding.root
    }

    private fun fromAllDataToEntry() {
        val list = ListForData.EverySoilHumDataList
        val currentDateList = mutableListOf<String>()
        val soilHum1List = mutableListOf<Entry>()
        val soilHum2List = mutableListOf<Entry>()
        val soilHum3List = mutableListOf<Entry>()
        val soilHum4List = mutableListOf<Entry>()
        val soilHum5List = mutableListOf<Entry>()
        val soilHum6List = mutableListOf<Entry>()
        val averageSoilHumList = mutableListOf<Entry>()

        val tempList1 = mutableListOf<Entry>()
        val tempList2 = mutableListOf<Entry>()
        val tempList3 = mutableListOf<Entry>()
        val tempList4 = mutableListOf<Entry>()
        val tempAverageList = mutableListOf<Entry>()
        val humList1 = mutableListOf<Entry>()
        val humList2 = mutableListOf<Entry>()
        val humList3 = mutableListOf<Entry>()
        val humList4 = mutableListOf<Entry>()
        val humAverageList = mutableListOf<Entry>()

        for (dt in 0 until list.size) {
            currentDateList.add(list[dt].currentData.split(' ')[1])
            soilHum1List.add(Entry(dt.toFloat(), list[dt].soilHum1.toFloat()))
            soilHum2List.add(Entry(dt.toFloat(), list[dt].soilHum2.toFloat()))
            soilHum3List.add(Entry(dt.toFloat(), list[dt].soilHum3.toFloat()))
            soilHum4List.add(Entry(dt.toFloat(), list[dt].soilHum4.toFloat()))
            soilHum5List.add(Entry(dt.toFloat(), list[dt].soilHum5.toFloat()))
            soilHum6List.add(Entry(dt.toFloat(), list[dt].soilHum6.toFloat()))

            tempList1.add(Entry(dt.toFloat(), list[dt].tempGreenhouse1.toFloat()))
            tempList2.add(Entry(dt.toFloat(), list[dt].tempGreenhouse2.toFloat()))
            tempList3.add(Entry(dt.toFloat(), list[dt].tempGreenhouse3.toFloat()))
            tempList4.add(Entry(dt.toFloat(), list[dt].tempGreenhouse4.toFloat()))

            humList1.add(Entry(dt.toFloat(), list[dt].humGreenhouse1.toFloat()))
            humList2.add(Entry(dt.toFloat(), list[dt].humGreenhouse2.toFloat()))
            humList3.add(Entry(dt.toFloat(), list[dt].humGreenhouse3.toFloat()))
            humList4.add(Entry(dt.toFloat(), list[dt].humGreenhouse4.toFloat()))

            averageSoilHumList.add(
                Entry(
                dt.toFloat(),
                (list[dt].soilHum1.toFloat() + list[dt].soilHum2.toFloat() +
                        list[dt].soilHum3.toFloat() + list[dt].soilHum4.toFloat()
                        + list[dt].soilHum5.toFloat() + list[dt].soilHum6.toFloat()) / 6
            ))

            tempAverageList.add(
                Entry(
                    dt.toFloat(),
                    (list[dt].tempGreenhouse1.toFloat() + list[dt].tempGreenhouse2.toFloat() +
                            list[dt].tempGreenhouse3.toFloat() + list[dt].tempGreenhouse4.toFloat()) / 4
                )
            )

            humAverageList.add(
                Entry(
                    dt.toFloat(),
                    (list[dt].humGreenhouse1.toFloat() + list[dt].humGreenhouse2.toFloat() +
                            list[dt].humGreenhouse3.toFloat() + list[dt].humGreenhouse4.toFloat()) / 4
                )
            )

        }

        val lineDataSetSoilHum1 = LineDataSet(soilHum1List, "Влажность в 1 бороздке")
        styleLineDataSet(lineDataSetSoilHum1, R.color.sienna)

        val lineDataSetSoilHum2 = LineDataSet(soilHum2List, "Влажность в 2 бороздке")
        styleLineDataSet(lineDataSetSoilHum2, R.color.celestial_blue)

        val lineDataSetSoilHum3 = LineDataSet(soilHum3List, "Влажность в 3 бороздке")
        styleLineDataSet(lineDataSetSoilHum3, R.color.indigo_dye)

        val lineDataSetSoilHum4 = LineDataSet(soilHum4List, "Влажность в 4 бороздке")
        styleLineDataSet(lineDataSetSoilHum4, R.color.persian_indigo)

        val lineDataSetSoilHum5 = LineDataSet(soilHum5List, "Влажность в 5 бороздке")
        styleLineDataSet(lineDataSetSoilHum5, R.color.saffron)

        val lineDataSetSoilHum6 = LineDataSet(soilHum6List, "Влажность в 6 бороздке")
        styleLineDataSet(lineDataSetSoilHum6, R.color.jade)

        val lineDataSetSoilHumAverage = LineDataSet(averageSoilHumList, "Средняя влажность почвы")
        styleLineDataSet(lineDataSetSoilHumAverage, R.color.sienna)

        val lineDate = mutableListOf<LineDataSet>()

        if (binding.firstSoilHum.isChecked) lineDate.add(lineDataSetSoilHum1)
        if (binding.secondSoilHum.isChecked) lineDate.add(lineDataSetSoilHum2)
        if (binding.thirdSoilHum.isChecked) lineDate.add(lineDataSetSoilHum3)
        if (binding.fourthSoilHum.isChecked) lineDate.add(lineDataSetSoilHum4)
        if (binding.fifthSoilHum.isChecked) lineDate.add(lineDataSetSoilHum5)
        if (binding.sixthSoilHum.isChecked) lineDate.add(lineDataSetSoilHum6)
        if (binding.zeroSoilHum.isChecked) lineDate.add(lineDataSetSoilHumAverage)


        binding.lcLineChart.data = LineData(lineDate.toList())
        binding.lcLineChart.setBackgroundColor(resources.getColor(R.color.white))
        binding.lcLineChart.xAxis.granularity = 1f
        binding.lcLineChart.xAxis.setDrawGridLines(false)
        val xAxis = binding.lcLineChart.xAxis
        xAxis.valueFormatter = IndexAxisValueFormatter(currentDateList)
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        binding.lcLineChart.isDragEnabled = true
        binding.lcLineChart.invalidate()
    }

//    private fun add_suitable(lineDataSet: LineDataSet): LineDataSet {
//    }

    private fun styleLineDataSet(lineDataSet: LineDataSet, colour: Int) = lineDataSet.apply {
        color = ContextCompat.getColor(requireContext(), colour)
        mode = LineDataSet.Mode.CUBIC_BEZIER
        setDrawCircles(false)
        valueTextSize = 17f
        lineWidth = 3f

    }

    private fun replaceFragment(fragment: Fragment) {
        val fragmentManager = childFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.chart_fragment, fragment)
        fragmentTransaction.commit()
    }
}