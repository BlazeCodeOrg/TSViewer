/*
 *
 *  * Copyright (c) BlazeCode / Ralf Lehmann, 2023.
 *
 */

package data

import com.blazecode.tsviewer.data.TsClient

data class WearDataPackage(
    val clients: List<TsClient>,
    val timestamp: Long
    )