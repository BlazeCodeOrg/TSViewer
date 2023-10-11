/*
 *
 *  * Copyright (c) BlazeCode / Ralf Lehmann, 2023.
 *
 */

package com.blazecode.tsviewer.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequest
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.WorkManager
import androidx.work.WorkRequest
import com.blazecode.tsviewer.data.TsChannel
import com.blazecode.tsviewer.database.DatabaseManager
import com.blazecode.tsviewer.uistate.HomeUiState
import com.blazecode.tsviewer.util.ConnectionManager
import com.blazecode.tsviewer.util.DemoModeValues
import com.blazecode.tsviewer.util.ServiceManager
import com.google.common.util.concurrent.ListenableFuture
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import util.ClientsWorker
import util.SettingsManager
import java.util.concurrent.ExecutionException
import java.util.concurrent.TimeUnit

class HomeViewModel(val app: Application) : AndroidViewModel(app) {

    private val settingsManager = SettingsManager(app)
    private val serviceManager = ServiceManager(app)

    // UI STATE
    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        if(!settingsManager.isDemoModeActive()){
            // DEFAULT OPERATION
            _uiState.value = _uiState.value.copy(serviceRunning = serviceManager.isRunning())
            _uiState.value = _uiState.value.copy(debug_updateAvailable = isDebugUpdateActive())

            if(areCredentialsSet()){
                viewModelScope.launch {
                    _uiState.value = _uiState.value.copy(channels = getChannels())
                }
                _uiState.value = _uiState.value.copy(areCredentialsSet = true)
            }

            viewModelScope.launch {
                _uiState.value = _uiState.value.copy(lastUpdate = getLastUpdate())
            }
        } else {
            // DEMO MODE
            _uiState.value = _uiState.value.copy(serviceRunning = true)
            _uiState.value = _uiState.value.copy(lastUpdate = 5)
            _uiState.value = _uiState.value.copy(areCredentialsSet = true)
            _uiState.value = _uiState.value.copy(channels = DemoModeValues.channels())
        }
    }

    // SETTERS
    fun setRunService(serviceRunning: Boolean){
        _uiState.value = _uiState.value.copy(serviceRunning = serviceRunning)

        if(serviceRunning){
            serviceManager.startService()
        } else {
            serviceManager.stopService()
        }
        _uiState.value = _uiState.value.copy(serviceRunning = serviceManager.isRunning())
    }

    private suspend fun getChannels(): MutableList<TsChannel> {
        var tempChannels = mutableListOf<TsChannel>()
        val job = viewModelScope.launch {
            val connectionmanager = ConnectionManager(app)
            tempChannels = connectionmanager.getChannels(settingsManager.getConnectionDetails())
        }
        job.join()
        return tempChannels
    }

    // GETTERS
    private fun areCredentialsSet(): Boolean {
        return settingsManager.areCredentialsSet()
    }

    private fun isDebugUpdateActive(): Boolean {
        return settingsManager.isDebugUpdateActive()
    }

    private suspend fun getLastUpdate(): Long {
        var timestamp: Long = 0
        val job = viewModelScope.launch(Dispatchers.IO) {
            val databaseManager = DatabaseManager(app)
            timestamp = databaseManager.getTimestampOfLastUpdate()
        }
        job.join()
        return if(timestamp == 0L) 0 else (System.currentTimeMillis() - timestamp) / 1000 / 60
    }
}