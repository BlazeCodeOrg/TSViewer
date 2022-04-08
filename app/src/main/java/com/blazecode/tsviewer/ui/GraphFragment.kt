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
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext


class GraphFragment(override val coroutineContext: CoroutineContext) : Fragment(), CoroutineScope {

    private var graphBinding: FragmentGraphBinding? = null
    private val binding get() = graphBinding!!

    lateinit var db: UserCountDatabase
    lateinit var userCountDAO: UserCountDAO

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

        val graph: BarChart = view.findViewById(R.id.chart)
        getDataFromDataBase(graph)
    }

    private fun getDataFromDataBase(barChart: BarChart){
        var dataTemp = mutableListOf<UserCount>()
        launch(Dispatchers.IO) {
            dataTemp = userCountDAO.getAll()
            remapData(dataTemp, barChart)
        }
    }

    private fun remapData(data: MutableList<UserCount>, barChart: BarChart){
        // CONVERT USERCOUNT TO ENTRIES
        var entries: MutableList<BarEntry> = mutableListOf()
        for(UserCount in data){
            entries.add(BarEntry(UserCount.timestamp!!.toFloat(), UserCount.amount!!.toFloat(), UserCount.names))
        }

        // STYLE DATASET
        val barChartDataset = BarDataSet(entries, "Clients")
        barChartDataset.color = R.color.text

        val barData = BarData(barChartDataset)
        styleGraph(barData, barChart)
    }

    private fun styleGraph(barData: BarData, barChart: BarChart){
        // SET GRAPH HEIGHT TO HALF OF THE SCREEN
        val display = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            requireActivity().display
        } else {
            requireActivity().windowManager.defaultDisplay
        }
        val size = Point()
        display!!.getSize(size)
        val params: ViewGroup.LayoutParams = barChart.layoutParams
        params.height = size.y / 2
        barChart.layoutParams = params

        // STYLE GRAPH
        barChart.setBackgroundColor(resources.getColor(R.color.background))
        barChart.setDrawBorders(false)
        barChart.description.text = ""

        assignDataToGraph(barData, barChart)
    }

    private fun assignDataToGraph(barData: BarData, barChart: BarChart){
        barChart.data = barData
    }
}