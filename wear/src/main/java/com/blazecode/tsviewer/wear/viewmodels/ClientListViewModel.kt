package com.blazecode.tsviewer.wear.viewmodels

import android.app.Application
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.Observer
import com.blazecode.tsviewer.wear.communication.WearDataManager
import com.blazecode.tsviewer.wear.uistate.ClientListUiState
import data.DataHolder
import data.TsClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class ClientListViewModel(val app: Application): AndroidViewModel(app) {

    // UI STATE
    private val _uiState = MutableStateFlow(ClientListUiState())
    val uiState: StateFlow<ClientListUiState> = _uiState.asStateFlow()

    init {
        loadData()

        val listObserver = Observer<MutableList<TsClient>> {
            loadData()
            _uiState.value = _uiState.value.copy(
                isLoading = false
            )
            vibrate()
        }
        DataHolder.list.observeForever(listObserver)
    }

    fun loadData(){
        val list = if(DataHolder.list.value == null) mutableListOf<TsClient>() else DataHolder.list.value!!
        val time = if(DataHolder.time.value == null) 0 else ((System.currentTimeMillis() - DataHolder.time.value!!) / 1000 / 60)

        _uiState.value = _uiState.value.copy(
            clientListString = list.map{ it.nickname }.joinToString(),
            clientList = list,
            time = time,
        )
    }

    fun requestRefresh(){
        _uiState.value = _uiState.value.copy(
            isLoading = true
        )

        WearDataManager(app).requestRefresh()
    }

    fun vibrate(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q){
            val vibrator = app.getSystemService(Vibrator::class.java)
            vibrator.vibrate(VibrationEffect.createOneShot(50, 120))
        }
    }
}