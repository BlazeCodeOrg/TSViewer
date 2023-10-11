package com.blazecode.tsviewer.wear.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.blazecode.tsviewer.wear.communication.WearDataManager
import com.blazecode.tsviewer.wear.uistate.ServiceOffUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class ServiceOffViewModel(val app: Application): AndroidViewModel(app) {

    // UI STATE
    private val _uiState = MutableStateFlow(ServiceOffUiState())
    val uiState: StateFlow<ServiceOffUiState> = _uiState.asStateFlow()

    fun startService(){
        WearDataManager(app).startService()
        _uiState.value = _uiState.value.copy(
            startServiceButtonEnabled = false
        )
    }

    fun launchApp(){
        WearDataManager(app).sendStartActivityRequest()
    }
}