/*
 *
 *  * Copyright (c) BlazeCode / Ralf Lehmann, 2023.
 *
 */

package com.blazecode.tsviewer.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import com.blazecode.eventtool.views.SwitchBar
import com.blazecode.tsviewer.R
import com.blazecode.tsviewer.ui.theme.TSViewerTheme
import com.blazecode.tsviewer.ui.theme.Typography
import com.blazecode.tsviewer.viewmodels.HomeViewModel
import com.blazecode.tsviewer.views.TsChannelList

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Home(viewModel: HomeViewModel = viewModel()) {
    TSViewerTheme {
        val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())
        Scaffold (
            topBar = { TopAppBar(scrollBehavior) },
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
private fun MainLayout(viewModel: HomeViewModel) {
    val uiState = viewModel.uiState.collectAsState()

    Column {
        SwitchBar(
            title = stringResource(R.string.run_service),
            checked = uiState.value.serviceRunning,
            onCheckChanged = { viewModel.setRunService(it) }
        )
        Box(modifier = Modifier.padding(dimensionResource(R.dimen.medium_padding))){
            TsChannelList(
                channels = uiState.value.channels,
                onClickChannel = { channel -> println(channel.toString()) },
                onClickMember = { member -> println(member.toString()) }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TopAppBar(scrollBehavior: TopAppBarScrollBehavior){
    LargeTopAppBar(
        title = { Text(text = stringResource(R.string.app_name), style = Typography.titleLarge) },
        scrollBehavior = scrollBehavior
    )
}