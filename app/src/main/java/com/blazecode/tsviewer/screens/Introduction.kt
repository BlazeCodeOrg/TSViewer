/*
 *
 *  * Copyright (c) BlazeCode / Ralf Lehmann, 2023.
 *
 */

package com.blazecode.tsviewer.screens

import android.Manifest
import android.os.Build
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.blazecode.eventtool.views.DefaultPreference
import com.blazecode.tsviewer.R
import com.blazecode.tsviewer.ui.theme.TSViewerTheme
import com.blazecode.tsviewer.viewmodels.IntroductionViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Introduction(viewModel: IntroductionViewModel = viewModel(), navController: NavController) {
    TSViewerTheme {
        val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())
        Scaffold (
            topBar = { TopAppBar(scrollBehavior) },
            modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
            content = { paddingValues ->
                Box(modifier = Modifier.padding(paddingValues).fillMaxSize()){
                    MainLayout(viewModel, navController)
                }
            }
        )
    }
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
private fun MainLayout(viewModel: IntroductionViewModel, navController: NavController) {
    val uiState = viewModel.uiState.collectAsState()

    // LIFECYCLE AWARENESS FOR CHANGING PERMISSION STATE
    val lifecycle = LocalLifecycleOwner.current.lifecycle

    val latestLifecycleEvent = remember { mutableStateOf(Lifecycle.Event.ON_ANY) }
    DisposableEffect(lifecycle) {
        val observer = LifecycleEventObserver { _, event ->
            latestLifecycleEvent.value = event
        }
        lifecycle.addObserver(observer)
        onDispose {
            lifecycle.removeObserver(observer)
        }
    }

    if(latestLifecycleEvent.value == Lifecycle.Event.ON_RESUME){
        LaunchedEffect(latestLifecycleEvent){
            viewModel.checkPermissions()
        }
    }

    //LAYOUT
    Column {
        // BATTERY OPTIMIZATION
        AnimatedVisibility(
            visible = !uiState.value.isBatteryOptimizationActive,
            enter = fadeIn(),
            exit = fadeOut()
        ){
            DefaultPreference(
                title = stringResource(R.string.turn_off_battery_optimization),
                summary = stringResource(R.string.turn_off_battery_optimization_summary),
                icon = painterResource(R.drawable.ic_battery),
                onClick = {
                    viewModel.askBatteryOptimization()
                }
            )
        }

        // NOTFICATION PERMISSION && QS TILE
        if(Build.VERSION.SDK_INT >= 33) {       // ANDROID 13 (API 33)
            val hasNotificationPermission = rememberPermissionState(Manifest.permission.POST_NOTIFICATIONS)

            AnimatedVisibility(
                visible = !hasNotificationPermission.status.isGranted,
                enter = fadeIn(),
                exit = fadeOut()
            ){
                DefaultPreference(
                    title = stringResource(R.string.notification_permission),
                    summary = stringResource(R.string.notification_permission_summary),
                    icon = painterResource(R.drawable.ic_notification),
                    onClick = {
                        hasNotificationPermission.launchPermissionRequest()
                    }
                )
            }

            AnimatedVisibility(
                visible = !uiState.value.placedQsTile,
                enter = fadeIn(),
                exit = fadeOut()
            ){
                DefaultPreference(
                    title = stringResource(R.string.place_QS_tile),
                    summary = stringResource(R.string.place_QS_tile_summary),
                    icon = painterResource(R.drawable.ic_qs_tile),
                    onClick = {
                        viewModel.placeQsTile()
                    }
                )
            }
        }
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TopAppBar(scrollBehavior: TopAppBarScrollBehavior){
    LargeTopAppBar(
        title = { Text(text = stringResource(R.string.introduction)) },
        scrollBehavior = scrollBehavior
    )
}