package com.blazecode.tsviewer.wear.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.blazecode.tsviewer.R
import com.blazecode.tsviewer.wear.communication.WearDataManager
import com.blazecode.tsviewer.wear.data.DataHolder
import com.blazecode.tsviewer.wear.enum.ErrorCode
import com.blazecode.tsviewer.wear.uistate.ErrorUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class ErrorViewModel(val app: Application): AndroidViewModel(app) {

    // UI STATE
    private val _uiState = MutableStateFlow(ErrorUiState())
    val uiState: StateFlow<ErrorUiState> = _uiState.asStateFlow()

    init {
        var errorString: String
        when (DataHolder.errorCode.value) {
            ErrorCode.NO_NETWORK -> {
                errorString = app.getString(R.string.no_network)
            }
            ErrorCode.NO_WIFI -> {
                errorString = app.getString(R.string.no_wifi)
            }
            ErrorCode.AIRPLANE_MODE -> {
                errorString = app.getString(R.string.airplane_mode)
            }
            else -> {
                errorString = ""
            }
        }

        _uiState.value = _uiState.value.copy(
            error = errorString
        )
    }

    fun launchApp(){
        WearDataManager(app).sendStartActivityRequest()
    }
}