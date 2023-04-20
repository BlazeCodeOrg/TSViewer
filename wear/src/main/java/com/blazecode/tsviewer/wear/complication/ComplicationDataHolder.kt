/*
 *
 *  * Copyright (c) BlazeCode / Ralf Lehmann, 2023.
 *
 */

package com.blazecode.tsviewer.wear.complication

import data.TsClient

object ComplicationDataHolder {
    var list: MutableList<TsClient> = mutableListOf()
    var time: Long = 0
}