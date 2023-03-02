/*
 *
 *  * Copyright (c) BlazeCode / Ralf Lehmann, 2023.
 *
 */

package com.blazecode.tsviewer.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.blazecode.tsviewer.uistate.SettingsUiState
import com.blazecode.tsviewer.util.ConnectionManager
import com.blazecode.tsviewer.util.SettingsManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class SettingsViewModel(val app: Application) : AndroidViewModel(app){

    // UI STATE
    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    // INIT
    init {
        loadSettings()
    }

    // NETWORK
    fun testConnection(){
        val connectionManager = ConnectionManager(app)
        val connectionResult = connectionManager.testConnection(
            ip = uiState.value.ip,
            username = uiState.value.username,
            password = uiState.value.password,
            queryPort = uiState.value.queryPort,
            virtualServerId = uiState.value.virtualServerId)

        _uiState.value = _uiState.value.copy(connectionSuccessful = connectionResult)
    }

    // SETTERS
    fun setScheduleTime(scheduleTime: Float){
        _uiState.value = _uiState.value.copy(scheduleTime = scheduleTime)
        saveSettings()
    }

    fun setIp(ip: String){
        _uiState.value = _uiState.value.copy(ip = ip, connectionSuccessful = null)
        saveSettings()
    }

    fun setUsername(username: String){
        _uiState.value = _uiState.value.copy(username = username, connectionSuccessful = null)
        saveSettings()
    }

    fun setPassword(password: String){
        _uiState.value = _uiState.value.copy(password = password, connectionSuccessful = null)
        saveSettings()
    }

    fun setQueryPort(queryPort: Int){
        _uiState.value = _uiState.value.copy(queryPort = queryPort, connectionSuccessful = null)
        saveSettings()
    }

    fun setVirtualServerId(virtualServerId: Int){
        _uiState.value = _uiState.value.copy(virtualServerId = virtualServerId, connectionSuccessful = null)
        saveSettings()
    }

    fun setIncludeQueryClients(includeQueryClients: Boolean){
        _uiState.value = _uiState.value.copy(includeQueryClients = includeQueryClients)
        saveSettings()
    }

    fun setExecuteOnlyOnWifi(executeOnlyOnWifi: Boolean){
        _uiState.value = _uiState.value.copy(executeOnlyOnWifi = executeOnlyOnWifi)
        saveSettings()
    }

    // SETTINGS
    private fun loadSettings(){
        val settingsManager = SettingsManager(app)
        _uiState.value = settingsManager.getSettingsUiState()
    }

    private fun saveSettings(){
        val settingsManager = SettingsManager(app)
        settingsManager.saveSettingsUiState(_uiState.value)
    }
}