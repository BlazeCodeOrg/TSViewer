/*
 *
 *  * Copyright (c) BlazeCode / Ralf Lehmann, 2023.
 *
 */

package com.blazecode.tsviewer.data

data class WearDataPackage(
    val clients: List<TsClient>,
    val timestamp: Long
    )