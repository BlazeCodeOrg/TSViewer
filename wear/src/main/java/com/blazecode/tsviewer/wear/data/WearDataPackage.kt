/*
 *
 *  * Copyright (c) BlazeCode / Ralf Lehmann, 2023.
 *
 */

package com.blazecode.tsviewer.wear.data

import com.blazecode.tsviewer.wear.enum.ErrorCode

data class WearDataPackage(
    val clients: List<TsClient>,
    val timestamp: Long,
    val errorCode: ErrorCode?
)