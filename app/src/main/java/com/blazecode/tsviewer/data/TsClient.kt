/*
 *
 *  * Copyright (c) BlazeCode / Ralf Lehmann, 2023.
 *
 */

package com.blazecode.tsviewer.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity
data class TsClient(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val nickname: String,
    val isInputMuted: Boolean = false,
    val isOutputMuted: Boolean = false,

    val lastSeen: Date = Date(),
    val activeConnectionTime: Long = 0,         // IN MINUTES
)
