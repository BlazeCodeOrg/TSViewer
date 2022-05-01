package com.blazecode.tsviewer.ui

import android.graphics.Point
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.transition.AutoTransition
import androidx.transition.TransitionManager
import com.blazecode.tsviewer.R
import com.blazecode.tsviewer.databinding.FragmentGraphBinding
import com.blazecode.tsviewer.util.database.UserCount
import com.blazecode.tsviewer.util.database.UserCountDAO
import com.blazecode.tsviewer.util.database.UserCountDatabase
import com.blazecode.tsviewer.util.graphmarker.CustomGraphMarker
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.google.android.material.dialog.MaterialAlertDialogBuilder
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
        graphBinding = FragmentGraphBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        getDataFromDataBase(binding.chart)

        binding.buttonDeleteDatabase.setOnClickListener {
            deleteDatabaseDialog()
        }
    }

    private fun getDataFromDataBase(lineChart: LineChart){
        var dataTemp = mutableListOf<UserCount>()
        launch(Dispatchers.IO) {
            dataTemp = userCountDAO.getAll()
            if(dataTemp.size >= 4){
                requireActivity().runOnUiThread {
                    remapData(dataTemp, lineChart)
                }
            } else {
                requireActivity().runOnUiThread {
                    binding.layoutLoading.root.isVisible = false
                    binding.layoutEmptyDatabase.root.isVisible = true
                }
            }
        }
    }

    private fun remapData(data: MutableList<UserCount>, lineChart: LineChart){
        // CONVERT USERCOUNT TO ENTRIES
        var entries: MutableList<Entry> = mutableListOf()
        for(UserCount in data){
            entries.add(Entry(UserCount.id!!.toFloat(), UserCount.amount!!.toFloat(), "${UserCount.timestamp};${UserCount.names}"))
            xAxisTime.add(convertUnixToTime(UserCount.timestamp!!))
        }

        // STYLE DATASET
        val lineChartDataSet = LineDataSet(entries, "Clients")
        lineChartDataSet.color = R.color.primary
        lineChartDataSet.mode = LineDataSet.Mode.HORIZONTAL_BEZIER
        lineChartDataSet.fillColor = requireContext().getColor(R.color.graph_fill)
        lineChartDataSet.fillAlpha = 255
        lineChartDataSet.setDrawFilled(true)
        lineChartDataSet.setDrawCircles(false)
        lineChartDataSet.setDrawValues(false)

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
        lineChart.setBackgroundColor(requireContext().getColor(R.color.background))
        lineChart.description.text = ""
        lineChart.legend.isEnabled = false
        lineChart.setHardwareAccelerationEnabled(true)

        //YAXIS STYLING
        lineChart.axisLeft.granularity = 1F
        lineChart.axisLeft.gridColor = requireContext().getColor(R.color.graph_grid)
        lineChart.axisLeft.textColor = requireContext().getColor(R.color.graph_text)
        lineChart.axisRight.isEnabled = false

        //XAXIS VALUE FORMATTING
        lineChart.xAxis.gridColor = requireContext().getColor(R.color.graph_grid)
        lineChart.xAxis.textColor = requireContext().getColor(R.color.graph_text)
        lineChart.xAxis.valueFormatter = IndexAxisValueFormatter(xAxisTime)

        assignDataToGraph(lineData, lineChart)

        //SETUP VIEW WINDOW
        lineChart.moveViewToX(xAxisTime.size.toFloat())                         //MOVE TO THE FAR RIGHT
        lineChart.setVisibleXRangeMaximum(96F)                                  //MAX X ZOOM OF 1 DAY AT MINIMUM SCHEDULE TIME OF 15 MINS

        //ADD CUSTOM MARKER
        lineChart.isHighlightPerTapEnabled = true
        lineChart.isHighlightPerDragEnabled = true
        val markerView = CustomGraphMarker(requireContext(), R.layout.marker_layout)
        lineChart.marker = markerView

    }

    private fun assignDataToGraph(lineData: LineData, lineChart: LineChart){
        lineChart.data = lineData

        //HIDE LOADING SCREEN, SHOW GRAPH
        binding.graphContainer.isVisible = true
        binding.layoutLoading.root.isVisible = false
    }

    private fun convertUnixToTime(unix: Long): String {
        val date = Date(unix)
        val simpleDateFormat = SimpleDateFormat("HH:mm")
        simpleDateFormat.timeZone = TimeZone.getDefault()
        return simpleDateFormat.format(date)
    }

    private fun deleteDatabaseDialog(){
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.delete_database)
            .setMessage(R.string.delete_database_message)
            .setNegativeButton(R.string.cancel) { dialog, which ->
                dialog.dismiss()
            }
            .setPositiveButton(R.string.yes) { dialog, which ->
                deleteDatabase()
                dialog.dismiss()
            }
            .show()
    }

    private fun deleteDatabase(){
        launch(Dispatchers.IO) {
            db.clearAllTables()
        }
        TransitionManager.beginDelayedTransition(binding.parentLayout, AutoTransition())
        binding.graphContainer.isVisible = false
        binding.layoutEmptyDatabase.root.isVisible = true
    }
}