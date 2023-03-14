/*
 *
 *  * Copyright (c) BlazeCode / Ralf Lehmann, 2023.
 *
 */

package com.blazecode.tsviewer.uistate

import com.blazecode.tsviewer.data.TsServerInfo

data class DataUiState(
    val serverInfoList: MutableList<TsServerInfo> = mutableListOf()
)
