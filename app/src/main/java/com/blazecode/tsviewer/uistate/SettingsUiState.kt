/*
 *
 *  * Copyright (c) BlazeCode / Ralf Lehmann, 2023.
 *
 */

package com.blazecode.tsviewer.uistate

data class SettingsUiState (
    val scheduleTime: Float = 15f,
    val ip: String = "ts.youripaddress.com",
    val username: String = "queryuser",
    val password: String = "password",
    val queryPort: Int = 10011,
    val includeQueryClients: Boolean = false,
    val executeOnlyOnWifi: Boolean = false,

    val connectionSuccessful: Boolean = false,
)