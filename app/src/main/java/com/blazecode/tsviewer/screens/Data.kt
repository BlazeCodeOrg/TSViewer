/*
 *
 *  * Copyright (c) BlazeCode / Ralf Lehmann, 2023.
 *
 */

package com.blazecode.tsviewer.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.blazecode.tsviewer.R
import com.blazecode.tsviewer.data.Entry
import com.blazecode.tsviewer.data.TsClient
import com.blazecode.tsviewer.data.TsServerInfo
import com.blazecode.tsviewer.navigation.NavRoutes
import com.blazecode.tsviewer.ui.theme.TSViewerTheme
import com.blazecode.tsviewer.ui.theme.Typography
import com.blazecode.tsviewer.util.graphmarker.rememberMarker
import com.blazecode.tsviewer.viewmodels.DataViewModel
import com.patrykandpatrick.vico.compose.axis.horizontal.bottomAxis
import com.patrykandpatrick.vico.compose.axis.vertical.startAxis
import com.patrykandpatrick.vico.compose.chart.Chart
import com.patrykandpatrick.vico.compose.chart.line.lineChart
import com.patrykandpatrick.vico.core.axis.AxisPosition
import com.patrykandpatrick.vico.core.axis.formatter.AxisValueFormatter
import com.patrykandpatrick.vico.core.chart.line.LineChart
import com.patrykandpatrick.vico.core.entry.ChartEntryModelProducer
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Data(viewModel: DataViewModel = viewModel(), navController: NavController) {
    TSViewerTheme {
        val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())
        Scaffold (
            topBar = { TopAppBar(scrollBehavior, navController) },
            modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
            content = { paddingValues ->
                Box(modifier = Modifier.padding(paddingValues).fillMaxSize()){
                    MainLayout(viewModel)
                }
            }
        )
    }
}

@Composable
private fun MainLayout(viewModel: DataViewModel) {
    val uiState = viewModel.uiState.collectAsState()
    Column(modifier = Modifier.padding(horizontal = 8.dp)) {
        Text(text = stringResource(R.string.utilization), style = Typography.titleMedium)
        ChartView(uiState.value.serverInfoList)
        Spacer(modifier = Modifier.size(16.dp))
        Text(text = stringResource(R.string.clients), style = Typography.titleMedium)
        ClientListView(
            inputList = uiState.value.clientList,
            onClick = {}
        )
    }
}

@Composable
private fun ChartView(inputList: List<TsServerInfo>){
    val list = inputList.reversed()
    val chartEntryModelProducer = list.mapIndexed { index, tsServerInfo ->
        Entry(index.toFloat(), tsServerInfo.clients.size.toFloat(), tsServerInfo) }
        .let { ChartEntryModelProducer(it) }

    val xAxisValueFormatter = AxisValueFormatter<AxisPosition.Horizontal.Bottom> { value, chartValues ->
        chartValues.chartEntryModel.entries.firstOrNull()?.getOrNull(value.toInt())?.let { entry ->

            val date = Date((entry as Entry).tsServerInfo.timestamp)
            val simpleDateFormat = SimpleDateFormat("HH")
            simpleDateFormat.timeZone = TimeZone.getDefault()
            simpleDateFormat.format(date)

        }.toString()
    }

    val yAxisValueFormatter = AxisValueFormatter<AxisPosition.Vertical.Start> { value, _ ->
        value.roundToInt().toString()
    }

    Chart(
        chart = lineChart(
            spacing = 1.dp,
            lines = listOf(
                LineChart.LineSpec(
                    pointSizeDp = 22f,
                )
            )
        ),
        model = chartEntryModelProducer.getModel(),
        marker = rememberMarker(),
        startAxis = startAxis(
            valueFormatter = yAxisValueFormatter,
        ),
        bottomAxis = bottomAxis(
            valueFormatter = xAxisValueFormatter,
            guideline = null,
        ),
    )
}

@Composable
private fun ClientListView(inputList: List<TsClient>, onClick: () -> Unit){
    val list = inputList.sortedBy { it.activeConnectionTime }.reversed()
    LazyColumn {
        items(list.size) { index ->
            ClientItemView(list[index], onClick = onClick)
        }
    }
}

@Composable
private fun ClientItemView(client: TsClient, onClick: () -> Unit){
    Card(modifier = Modifier.padding(vertical = 4.dp, horizontal = 2.dp).clickable(onClick = { onClick() })) {
        Row(modifier = Modifier.fillMaxWidth().padding(4.dp), horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
            Text(text = client.nickname)
            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.CenterEnd){
                Text(text = client.activeConnectionTime.toString())
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TopAppBar(scrollBehavior: TopAppBarScrollBehavior, navController: NavController){
    LargeTopAppBar(
        title = { Text(text = stringResource(R.string.data), style = Typography.titleLarge) },
        navigationIcon = {
            Box (modifier = Modifier.size(dimensionResource(R.dimen.icon_button_size)).clickable { navController.navigate(NavRoutes.Home.route) },
                contentAlignment = Alignment.Center){
                Icon(painterResource(R.drawable.ic_back), "back")
            }
        },
        scrollBehavior = scrollBehavior
    )
}