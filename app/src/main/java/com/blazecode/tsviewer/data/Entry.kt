/*
 *
 *  * Copyright (c) BlazeCode / Ralf Lehmann, 2023.
 *
 */

package com.blazecode.tsviewer.data

import com.patrykandpatrick.vico.core.entry.ChartEntry

class Entry(
    override val x: Float,
    override val y: Float,
    val tsServerInfo: TsServerInfo
) : ChartEntry {
    override fun withY(y: Float) = Entry(x, y, tsServerInfo)
}
