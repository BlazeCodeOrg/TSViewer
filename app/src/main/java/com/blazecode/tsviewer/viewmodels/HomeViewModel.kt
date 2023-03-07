/*
 *
 *  * Copyright (c) BlazeCode / Ralf Lehmann, 2023.
 *
 */

package com.blazecode.tsviewer.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.*
import com.blazecode.tsviewer.data.TsChannel
import com.blazecode.tsviewer.uistate.HomeUiState
import com.blazecode.tsviewer.util.ClientsWorker
import com.blazecode.tsviewer.util.ConnectionManager
import com.blazecode.tsviewer.util.SettingsManager
import com.google.common.util.concurrent.ListenableFuture
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.concurrent.ExecutionException
import java.util.concurrent.TimeUnit

class HomeViewModel(val app: Application) : AndroidViewModel(app) {

    private val TAG = "scheduleClients"
    private val settingsManager = SettingsManager(app)
    private var workManager: WorkManager = app.let { WorkManager.getInstance(it) }

    // UI STATE
    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        _uiState.value = _uiState.value.copy(serviceRunning = isRunning())

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(channels = getChannels())
        }
    }

    // SETTERS
    fun setRunService(serviceRunning: Boolean){
        _uiState.value = _uiState.value.copy(serviceRunning = serviceRunning)

        if(serviceRunning){
            startService()
        } else {
            stopService()
        }
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

    private fun startService(){
        val clientWorkRequest: PeriodicWorkRequest = PeriodicWorkRequestBuilder<ClientsWorker>(
            settingsManager.getScheduleTime().toLong(),                                                                                 //GIVE NEW WORK TIME
            TimeUnit.MINUTES,
            1, TimeUnit.MINUTES)                                                                      //FLEX TIME INTERVAL
            .build()

        val oneTimeclientWorkRequest: WorkRequest = OneTimeWorkRequestBuilder<ClientsWorker>().build()          //RUN ONE TIME
        workManager.enqueue(oneTimeclientWorkRequest)
        workManager.enqueueUniquePeriodicWork(TAG, ExistingPeriodicWorkPolicy.REPLACE, clientWorkRequest)     //SCHEDULE THE NEXT RUNS
    }

    private fun stopService(){
        workManager.cancelUniqueWork(TAG)
    }

    private fun isRunning() : Boolean {
        val instance = WorkManager.getInstance(app.applicationContext)
        val statuses: ListenableFuture<List<WorkInfo>> = instance.getWorkInfosForUniqueWork(TAG)
        return try {
            var running = false
            val workInfoList: List<WorkInfo> = statuses.get()
            for (workInfo in workInfoList) {
                val state = workInfo.state
                running = state == WorkInfo.State.RUNNING || state == WorkInfo.State.ENQUEUED
            }
            running
        } catch (e: ExecutionException) {
            e.printStackTrace()
            false
        } catch (e: InterruptedException) {
            e.printStackTrace()
            false
        }
    }
}