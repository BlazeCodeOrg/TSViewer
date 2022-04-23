package com.blazecode.tsviewer.ui

import android.graphics.Point
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.blazecode.tsviewer.R
import com.blazecode.tsviewer.databinding.FragmentGraphBinding
import com.blazecode.tsviewer.util.database.UserCount
import com.blazecode.tsviewer.util.database.UserCountDAO
import com.blazecode.tsviewer.util.database.UserCountDatabase
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.IAxisValueFormatter
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import kotlin.coroutines.CoroutineContext


class GraphFragment(override val coroutineContext: CoroutineContext) : Fragment(), CoroutineScope {

    private var graphBinding: FragmentGraphBinding? = null
    private val binding get() = graphBinding!!

    lateinit var db: UserCountDatabase
    lateinit var userCountDAO: UserCountDAO

    var xAxisTime: MutableList<String> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // OPEN DB
        db = UserCountDatabase.build(requireContext())
        userCountDAO = db.userCountDao()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_graph, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val graph: LineChart = view.findViewById(R.id.chart)
        getDataFromDataBase(graph)
    }

    private fun getDataFromDataBase(lineChart: LineChart){
        var dataTemp = mutableListOf<UserCount>()
        launch(Dispatchers.IO) {
            dataTemp = userCountDAO.getAll()
            requireActivity().runOnUiThread {
                remapData(dataTemp, lineChart)
            }
        }
    }

    private fun remapData(data: MutableList<UserCount>, lineChart: LineChart){
        // CONVERT USERCOUNT TO ENTRIES
        var entries: MutableList<Entry> = mutableListOf()
        for(UserCount in data){
            entries.add(Entry(UserCount.id!!.toFloat(), UserCount.amount!!.toFloat(), UserCount.names))
            xAxisTime.add(convertUnixToTime(UserCount.timestamp!!))
        }

        // STYLE DATASET
        val lineChartDataSet = LineDataSet(entries, "Clients")
        lineChartDataSet.color = R.color.primary
        lineChartDataSet.mode = LineDataSet.Mode.HORIZONTAL_BEZIER
        lineChartDataSet.fillColor = R.color.primary
        lineChartDataSet.setDrawFilled(true)
        lineChartDataSet.setDrawCircles(false)

        val lineData = LineData(lineChartDataSet)
        styleGraph(lineData, lineChart)
    }

    private fun styleGraph(lineData: LineData, lineChart: LineChart){
        // SET GRAPH HEIGHT TO HALF OF THE SCREEN
        val display = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            requireActivity().display
        } else {
            requireActivity().windowManager.defaultDisplay
        }
        val size = Point()
        display!!.getSize(size)
        val params: ViewGroup.LayoutParams = lineChart.layoutParams
        params.height = size.y / 2
        lineChart.layoutParams = params

        // STYLE GRAPH
        lineChart.setBackgroundColor(resources.getColor(R.color.background))
        lineChart.description.text = ""
        lineChart.legend.isEnabled = false
        lineChart.setHardwareAccelerationEnabled(true)

        //XAXIS VALUE FORMATTING
        val xAxis = lineChart.xAxis
        xAxis.valueFormatter = IndexAxisValueFormatter(xAxisTime)

        assignDataToGraph(lineData, lineChart)
    }

    private fun assignDataToGraph(lineData: LineData, lineChart: LineChart){
        lineChart.data = lineData
    }

    private fun convertUnixToTime(unix: Long): String {
        val date = Date(unix)
        val simpleDateFormat = SimpleDateFormat("HH:mm")
        simpleDateFormat.timeZone = TimeZone.getDefault()
        return simpleDateFormat.format(date)
    }
}