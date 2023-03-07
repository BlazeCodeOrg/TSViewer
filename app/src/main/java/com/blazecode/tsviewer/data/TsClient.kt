/*
 *
 *  * Copyright (c) BlazeCode / Ralf Lehmann, 2023.
 *
 */

package com.blazecode.tsviewer.data

data class TsClient(
    val nickname: String,
    val isInputMuted: Boolean = false,
    val isOutputMuted: Boolean = false
)
