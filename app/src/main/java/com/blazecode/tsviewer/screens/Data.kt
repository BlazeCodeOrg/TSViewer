/*
 *
 *  * Copyright (c) BlazeCode / Ralf Lehmann, 2023.
 *
 */

package com.blazecode.tsviewer.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.blazecode.tsviewer.R
import com.blazecode.tsviewer.navigation.NavRoutes
import com.blazecode.tsviewer.ui.theme.TSViewerTheme
import com.blazecode.tsviewer.ui.theme.Typography
import com.blazecode.tsviewer.viewmodels.DataViewModel

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

    Column {
        Text(text = "Data")
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