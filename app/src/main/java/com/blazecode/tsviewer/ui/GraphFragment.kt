package com.blazecode.tsviewer.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.blazecode.tsviewer.R
import com.blazecode.tsviewer.databinding.ActivityMainBinding
import com.blazecode.tsviewer.databinding.FragmentGraphBinding
import com.blazecode.tsviewer.databinding.MainFragmentBinding
import com.blazecode.tsviewer.util.database.UserCount
import com.blazecode.tsviewer.util.database.UserCountDAO
import com.blazecode.tsviewer.util.database.UserCountDatabase
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.Entry
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

class GraphFragment(override val coroutineContext: CoroutineContext) : Fragment(), CoroutineScope {

    private var graphBinding: FragmentGraphBinding? = null
    private val binding get() = graphBinding!!

    lateinit var db: UserCountDatabase
    lateinit var userCountDAO: UserCountDAO

    lateinit var barData : BarData

    var data = mutableListOf<UserCount>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        db = UserCountDatabase.build(requireContext())
        userCountDAO = db.userCountDao()

        launch {
            data = userCountDAO.getAll()
        }

        var entries: MutableList<BarEntry> = mutableListOf()
        for(UserCount in data){
            entries.add(BarEntry(UserCount.timestamp!!.toFloat(), UserCount.amount!!.toFloat(), UserCount.names))
        }
        val barChartDataset = BarDataSet(entries, "Clients")
        barChartDataset.color = R.color.primary

        barData = BarData(barChartDataset)
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
        graph.data = barData
    }
}