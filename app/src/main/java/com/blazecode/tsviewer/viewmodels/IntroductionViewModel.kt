/*
 *
 *  * Copyright (c) BlazeCode / Ralf Lehmann, 2023.
 *
 */

package com.blazecode.tsviewer.viewmodels

import android.app.Application
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.PowerManager
import android.provider.Settings
import androidx.lifecycle.AndroidViewModel
import com.blazecode.tsviewer.uistate.IntroductionUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class IntroductionViewModel(val app: Application) : AndroidViewModel(app) {

    // UI STATE
    private val _uiState = MutableStateFlow(IntroductionUiState())
    val uiState: StateFlow<IntroductionUiState> = _uiState.asStateFlow()

    init {
        checkPermissions()
    }

    fun checkPermissions(){
        _uiState.value = _uiState.value.copy(
            isBatteryOptimizationActive = isBatteryOptimizationActive(),
        )
    }

    // GETTERS

    fun isBatteryOptimizationActive(): Boolean {
        val powerManager: PowerManager = app.getSystemService(Context.POWER_SERVICE) as PowerManager
        return powerManager.isIgnoringBatteryOptimizations(app.packageName)
    }

    // SETTERS

    fun askBatteryOptimization(){
        val intent = Intent()
        intent.action = Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS
        intent.data = Uri.parse("package:${app.packageName}")
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        app.startActivity(intent)
    }
}