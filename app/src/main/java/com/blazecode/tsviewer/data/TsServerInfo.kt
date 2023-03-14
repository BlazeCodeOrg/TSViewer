/*
 *
 *  * Copyright (c) BlazeCode / Ralf Lehmann, 2023.
 *
 */

package com.blazecode.tsviewer.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class TsServerInfo (
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val timestamp: Long,
    val clients: MutableList<TsClient>
)