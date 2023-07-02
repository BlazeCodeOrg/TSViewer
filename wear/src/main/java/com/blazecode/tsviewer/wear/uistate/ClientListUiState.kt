package com.blazecode.tsviewer.wear.uistate

import data.TsClient

data class ClientListUiState (
    val clientListString: String = "",
    val clientList: MutableList<TsClient> = mutableListOf(),
    val time: Long = 0,
    val isLoading: Boolean = false,
    val isDoneLoading: Boolean = false
)