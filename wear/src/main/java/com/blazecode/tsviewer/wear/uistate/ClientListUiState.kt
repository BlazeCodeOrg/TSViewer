package com.blazecode.tsviewer.wear.uistate

import com.blazecode.tsviewer.wear.data.TsClient

data class ClientListUiState (
    val clientListString: String = "",
    var clientList: MutableList<TsClient> = mutableListOf(),
    val time: Long = 0,
    val isLoading: Boolean = false
)