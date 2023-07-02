package com.blazecode.tsviewer.wear.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.blazecode.tsviewer.wear.communication.WearDataManager
import com.blazecode.tsviewer.wear.uistate.ClientListUiState
import data.DataHolder
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class ClientListViewModel(val app: Application): AndroidViewModel(app) {

    // UI STATE
    private val _uiState = MutableStateFlow(ClientListUiState())
    val uiState: StateFlow<ClientListUiState> = _uiState.asStateFlow()

    init {
        _uiState.value = _uiState.value.copy(
            clientListString = DataHolder.list.map{ it.nickname }.joinToString(),
            clientList = DataHolder.list,
            time = if(DataHolder.time == 0L) 0 else ((System.currentTimeMillis() - DataHolder.time) / 1000 / 60)
        )
    }

    fun requestRefresh(){
        _uiState.value = _uiState.value.copy(
            isLoading = true
        )

        WearDataManager(app).requestRefresh()
    }
}