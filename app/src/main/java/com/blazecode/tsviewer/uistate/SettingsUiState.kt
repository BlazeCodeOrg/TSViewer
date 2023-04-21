/*
 *
 *  * Copyright (c) BlazeCode / Ralf Lehmann, 2023.
 *
 */

package com.blazecode.tsviewer.uistate

data class SettingsUiState (
    val scheduleTime: Float = 15f,
    val executeOnlyOnWifi: Boolean = false,
    val includeQueryClients: Boolean = false,
    val syncWearable: Boolean = false,

    val ip: String = "ts.youripaddress.com",
    val username: String = "queryuser",
    val password: String = "password",
    val queryPort: Int = 10011,
    val virtualServerId: Int = 1,

    val connectionSuccessful: Boolean? = null,
    val foundWearable: Boolean = false
)