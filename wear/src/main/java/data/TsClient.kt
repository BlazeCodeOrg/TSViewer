/*
 *
 *  * Copyright (c) BlazeCode / Ralf Lehmann, 2023.
 *
 */

package data

import java.util.Date

data class TsClient(
    val id: Int = 0,
    val nickname: String,
    val isInputMuted: Boolean = false,
    val isOutputMuted: Boolean = false,

    val lastSeen: Date = Date(),
    val activeConnectionTime: Long = 0,         // IN MINUTES
)
