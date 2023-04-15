/*
 *
 *  * Copyright (c) BlazeCode / Ralf Lehmann, 2023.
 *
 */

package com.blazecode.tsviewer.uistate

import com.blazecode.tsviewer.data.TsChannel

data class HomeUiState (
     val serviceRunning: Boolean = false,
     val channels: List<TsChannel> = listOf(),
     val areCredentialsSet: Boolean = false,
     val debug_forceNoCredentials: Boolean = false
)