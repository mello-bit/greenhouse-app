package com.example.greenhouse_app.fragments

import android.app.DatePickerDialog
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import androidx.core.content.ContextCompat
import com.example.greenhouse_app.MyApplication
import com.example.greenhouse_app.R
import com.example.greenhouse_app.dataClasses.AllData
import com.example.greenhouse_app.dataClasses.ListForData.Companion.EverySoilHumDataList
import com.example.greenhouse_app.dataClasses.ListForData.Companion.averageSoilHumList
import com.example.greenhouse_app.dataClasses.ListForData.Companion.currentDateList
import com.example.greenhouse_app.dataClasses.ListForData.Companion.humAverageList
import com.example.greenhouse_app.dataClasses.ListForData.Companion.humList1
import com.example.greenhouse_app.dataClasses.ListForData.Companion.humList2
import com.example.greenhouse_app.dataClasses.ListForData.Companion.humList3
import com.example.greenhouse_app.dataClasses.ListForData.Companion.humList4
import com.example.greenhouse_app.dataClasses.ListForData.Companion.soilHum1List
import com.example.greenhouse_app.dataClasses.ListForData.Companion.soilHum2List
import com.example.greenhouse_app.dataClasses.ListForData.Companion.soilHum3List
import com.example.greenhouse_app.dataClasses.ListForData.Companion.soilHum4List
import com.example.greenhouse_app.dataClasses.ListForData.Companion.soilHum5List
import com.example.greenhouse_app.dataClasses.ListForData.Companion.soilHum6List
import com.example.greenhouse_app.dataClasses.ListForData.Companion.tempAverageList
import com.example.greenhouse_app.dataClasses.ListForData.Companion.tempList1
import com.example.greenhouse_app.dataClasses.ListForData.Companion.tempList2
import com.example.greenhouse_app.dataClasses.ListForData.Companion.tempList3
import com.example.greenhouse_app.dataClasses.ListForData.Companion.tempList4
import com.example.greenhouse_app.databinding.FragmentChartBinding
import com.example.greenhouse_app.utils.*
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import kotlinx.coroutines.*
import java.lang.Runnable
import java.text.SimpleDateFormat
import java.util.*
import kotlin.properties.Delegates


class ChartFragment : Fragment() {

    private lateinit var binding: FragmentChartBinding
    private lateinit var handler: Handler
    private var list = EverySoilHumDataList
    private var hour: Int? = null
    private var date: String? = null
    var interval by Delegates.notNull<Int>()

    private lateinit var db: AppDatabase
    private lateinit var sensorDao: SensorDao

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentChartBinding.inflate(inflater, container, false)
        handler = Handler(Looper.getMainLooper())
        interval = AppSettingsManager.loadData("Interval")!!.toInt()

        binding.ibTableButton.setOnClickListener {
            val tableFragment = ChartsFragment()
            replaceFragment(tableFragment)
        }

        handler.post(object : Runnable {
            override fun run() {
                fromAllDataToEntry()
                handler.postDelayed(this, 200L)
            }
        })

        binding.chooseTheDate.setOnClickListener {
            showDatePickerDialog()
        }

        binding.chooseTheTime.setOnClickListener {
            showTimePickerDialog()
        }

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
        }
    }

    private fun showDatePickerDialog() {
        val myCalendar = Calendar.getInstance()
        val datePicker = DatePickerDialog.OnDateSetListener { _, year, month, day ->
            myCalendar.set(Calendar.YEAR, year)
            myCalendar.set(Calendar.MONTH, month)
            myCalendar.set(Calendar.DAY_OF_MONTH, day)
            formatterDate(myCalendar)

            CoroutineScope(Dispatchers.IO).launch {
                val application = requireActivity().applicationContext as MyApplication

                db = AppDatabaseHelper.getDatabase(requireContext(), application.currentUID)
                sensorDao = db.SensorDao()
                Log.d("CheckList", date!!)
                val dataList = sensorDao.getSensorDataForDate(date!!)
//                handler.removeCallbacksAndMessages(null)
                fromSensorDaoToAllData(dataList)
                Log.d("CheckList", list.toString())
                fromAllDataToEntry()

                withContext(Dispatchers.Main) {
                    fromAllDataToEntry()
                    delay(200L)
                }

                Log.d("CheckDateTime", dataList.toString())
            }
        }

        DatePickerDialog(requireContext(), datePicker,
            myCalendar.get(Calendar.YEAR),
            myCalendar.get(Calendar.MONTH),
            myCalendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    private fun fromSensorDaoToAllData(dataList: List<SensorData>) {
        val result = mutableListOf<AllData>()

        for (sd in dataList.indices) {
            result.add(
                AllData(
//                    dataList[sd].createdAt.split('T')[1].split('.')[0],
                    dataList[sd].createdAt.replace('T', ' '),
                    dataList[sd].furrow1_humidity,
                    dataList[sd].furrow2_humidity,
                    dataList[sd].furrow3_humidity,
                    dataList[sd].furrow4_humidity,
                    dataList[sd].furrow5_humidity,
                    dataList[sd].furrow6_humidity,
                    dataList[sd].greenhouse_temperature,
                    dataList[sd].greenhouse_temperature,
                    dataList[sd].greenhouse_temperature,
                    dataList[sd].greenhouse_temperature,
                    dataList[sd].greenhouse_humidity,
                    dataList[sd].greenhouse_humidity,
                    dataList[sd].greenhouse_humidity,
                    dataList[sd].greenhouse_humidity,
                )
            )
        }
        Log.d("CheckList", result.toString())
        list = result
    }

    private fun formatterDate(calendar: Calendar) {
        val myFormat = "yyyy-MM-dd"
        val sdf = SimpleDateFormat(myFormat, Locale.UK)
        date = sdf.format(calendar.time)
    }

    private fun fromAllDataToEntry() {

        clearSoilHumLists()
        clearTempAndHumLists()

        Log.d("CheckList", list.toString())
        for (dt in 0 until list.size) {

            currentDateList.add(list[dt].currentData.split(' ')[1])

            fillInAllSoilHumLists(dt)
            fillInAllTempAndHumLists(dt)
            fillInAllAverageLists(dt)

        }

        val allLineDataSetSoilHum = getAllLineDataSetSoilHum()
        val allLineDataSetTemp = getAllLineDataSetTemp()
        val allLineDataSetHum = getAllLineDataSetHum()

        val checkedSoilHumList = getCheckedSoilHumList()
        val checkedTempList = getCheckedTempList()
        val checkedHumList = getCheckedHumList()

        val filteredSoilHum = allLineDataSetSoilHum.zip(checkedSoilHumList)
            .filter { it.second }.map { it.first }

        val filteredTemp = allLineDataSetTemp.zip(checkedTempList)
            .filter { it.second }.map { it.first }

        val filteredHum = allLineDataSetHum.zip(checkedHumList)
            .filter { it.second }.map { it.first }

        val lineDate = mutableListOf<LineDataSet>()
        lineDate.addAll(filteredSoilHum)
        lineDate.addAll(filteredTemp)
        lineDate.addAll(filteredHum)


        binding.lcLineChart.data = LineData(lineDate.toList())

        styleChart()

        binding.lcLineChart.invalidate()
    }

    private fun styleChart() {
        binding.lcLineChart.apply {
            setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.white))
            isDragEnabled = true
            xAxis.apply {
                setDrawGridLines(false)
                granularity = 1f
                valueFormatter = IndexAxisValueFormatter(currentDateList)
                position = XAxis.XAxisPosition.BOTTOM
            }
        }
    }

    private fun styleLineDataSet(lineDataSet: LineDataSet, colour: Int) = lineDataSet.apply {
        color = ContextCompat.getColor(requireContext(), colour)
        mode = LineDataSet.Mode.CUBIC_BEZIER
        setDrawCircles(false)
        valueTextSize = 17f
        lineWidth = 3f

    }


    private fun getCheckedHumList(): List<Boolean> {
        val checked = mutableListOf<Boolean>()

        for (child in 0 until binding.humCheckboxes.childCount) {
            val checkbox = binding.humCheckboxes.getChildAt(child) as CheckBox

            checked.add(checkbox.isChecked)
        }

        return checked
    }

    private fun getCheckedTempList(): List<Boolean> {
        val checked = mutableListOf<Boolean>()

        for (child in 0 until binding.tempCheckboxes.childCount) {
            val checkbox = binding.tempCheckboxes.getChildAt(child) as CheckBox

            checked.add(checkbox.isChecked)
        }

        return checked
    }

    private fun getCheckedSoilHumList(): List<Boolean> {
        val checked = mutableListOf<Boolean>()

        for (child in 0 until binding.soilHumCheckboxes.childCount) {
            val checkbox = binding.soilHumCheckboxes.getChildAt(child) as CheckBox

            checked.add(checkbox.isChecked)
        }

        return checked
    }

    private fun getAllLineDataSetTemp(): List<LineDataSet> {
        val lineDataSetTemp1 = LineDataSet(tempList1, "Температура с 1 датчика")
        styleLineDataSet(lineDataSetTemp1, R.color.apple_green)

        val lineDataSetTemp2 = LineDataSet(tempList2, "Температура с 2 датчика")
        styleLineDataSet(lineDataSetTemp2, R.color.coral)

        val lineDataSetTemp3 = LineDataSet(tempList3, "Температура с 3 датчика")
        styleLineDataSet(lineDataSetTemp3, R.color.peach)

        val lineDataSetTemp4 = LineDataSet(tempList4, "Температура с 4 датчика")
        styleLineDataSet(lineDataSetTemp4, R.color.walnut_brown)

        val lineDataSetTempAverage = LineDataSet(tempAverageList, "Средняя температура с датчиков")
        styleLineDataSet(lineDataSetTempAverage, R.color.mindaro)

        return listOf(
            lineDataSetTempAverage, lineDataSetTemp1, lineDataSetTemp2, lineDataSetTemp3, lineDataSetTemp4
        )

    }

    private fun getAllLineDataSetHum(): List<LineDataSet> {
        val lineDataSetHum1 = LineDataSet(humList1, "Влажность с 1 датчика")
        styleLineDataSet(lineDataSetHum1, R.color.gunmetal)

        val lineDataSetHum2 = LineDataSet(humList2, "Влажность с 2 датчика")
        styleLineDataSet(lineDataSetHum2, R.color.sage)

        val lineDataSetHum3 = LineDataSet(humList3, "Влажность с 3 датчика")
        styleLineDataSet(lineDataSetHum3, R.color.citrine)

        val lineDataSetHum4 = LineDataSet(humList4, "Влажность с 4 датчика")
        styleLineDataSet(lineDataSetHum4, R.color.red_crayola)

        val lineDataSetHumAverage = LineDataSet(humAverageList, "Средняя влажность с датчиков")
        styleLineDataSet(lineDataSetHumAverage, R.color.thistle)

        return listOf(
            lineDataSetHumAverage, lineDataSetHum1, lineDataSetHum2, lineDataSetHum3, lineDataSetHum4
        )
    }

    private fun getAllLineDataSetSoilHum(): List<LineDataSet> {
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

        return listOf(
            lineDataSetSoilHumAverage, lineDataSetSoilHum1, lineDataSetSoilHum2, lineDataSetSoilHum3,
            lineDataSetSoilHum4, lineDataSetSoilHum5, lineDataSetSoilHum6
        )
    }

    private fun fillInAllSoilHumLists(dt: Int) {
        soilHum1List.add(Entry(dt.toFloat(), list[dt].soilHum1.toFloat()))
        soilHum2List.add(Entry(dt.toFloat(), list[dt].soilHum2.toFloat()))
        soilHum3List.add(Entry(dt.toFloat(), list[dt].soilHum3.toFloat()))
        soilHum4List.add(Entry(dt.toFloat(), list[dt].soilHum4.toFloat()))
        soilHum5List.add(Entry(dt.toFloat(), list[dt].soilHum5.toFloat()))
        soilHum6List.add(Entry(dt.toFloat(), list[dt].soilHum6.toFloat()))
    }

    private fun fillInAllTempAndHumLists(dt: Int) {
        tempList1.add(Entry(dt.toFloat(), list[dt].tempGreenhouse1.toFloat()))
        tempList2.add(Entry(dt.toFloat(), list[dt].tempGreenhouse2.toFloat()))
        tempList3.add(Entry(dt.toFloat(), list[dt].tempGreenhouse3.toFloat()))
        tempList4.add(Entry(dt.toFloat(), list[dt].tempGreenhouse4.toFloat()))

        humList1.add(Entry(dt.toFloat(), list[dt].humGreenhouse1.toFloat()))
        humList2.add(Entry(dt.toFloat(), list[dt].humGreenhouse2.toFloat()))
        humList3.add(Entry(dt.toFloat(), list[dt].humGreenhouse3.toFloat()))
        humList4.add(Entry(dt.toFloat(), list[dt].humGreenhouse4.toFloat()))
    }

    private fun fillInAllAverageLists(dt: Int) {
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

    private fun clearSoilHumLists() {
        soilHum1List.clear()
        soilHum2List.clear()
        soilHum3List.clear()
        soilHum4List.clear()
        soilHum5List.clear()
        soilHum6List.clear()
        averageSoilHumList.clear()
    }

    private fun clearTempAndHumLists() {
        tempList1.clear()
        tempList2.clear()
        tempList3.clear()
        tempList4.clear()
        tempAverageList.clear()
        humList1.clear()
        humList2.clear()
        humList3.clear()
        humList4.clear()
        humAverageList.clear()
    }

    private fun replaceFragment(fragment: Fragment) {
        val fragmentManager = childFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.chart_fragment, fragment)
        fragmentTransaction.commit()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        handler.removeCallbacksAndMessages(null)
    }
}