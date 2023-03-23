/*
 *
 *  * Copyright (c) BlazeCode / Ralf Lehmann, 2023.
 *
 */

package com.blazecode.tsviewer.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.blazecode.tsviewer.data.TsClient
import com.blazecode.tsviewer.data.TsServerInfo
import com.blazecode.tsviewer.database.ClientRepository
import com.blazecode.tsviewer.database.ServerRepository
import com.blazecode.tsviewer.uistate.DataUiState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class DataViewModel(val app: Application): AndroidViewModel(app){

    // UI STATE
    private val _uiState = MutableStateFlow(DataUiState())
    val uiState: StateFlow<DataUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                serverInfoList = getServerInfoList(),
                clientList = getClientList()
            )
        }
    }

    private suspend fun getServerInfoList(): MutableList<TsServerInfo> {
        var list = mutableListOf<TsServerInfo>()
        val job = viewModelScope.launch(Dispatchers.IO) {
            val repository = ServerRepository(app)
            list = repository.getServerInfo()
        }
        job.join()
        return list
    }

    private suspend fun getClientList(): MutableList<TsClient> {
        var list = mutableListOf<TsClient>()
        val job = viewModelScope.launch(Dispatchers.IO) {
            val repository = ClientRepository(app)
            list = repository.getAllClients()
        }
        job.join()
        return list
    }
}