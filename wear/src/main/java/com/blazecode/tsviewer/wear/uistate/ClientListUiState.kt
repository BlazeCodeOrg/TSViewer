package com.blazecode.tsviewer.wear.uistate

import data.TsClient

data class ClientListUiState (
    val clientListString: String = "",
    var clientList: MutableList<TsClient> = mutableListOf(),
    val time: Long = 0,
    val isLoading: Boolean = false
)