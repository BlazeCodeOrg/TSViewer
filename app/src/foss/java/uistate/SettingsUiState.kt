/*
 *
 *  * Copyright (c) BlazeCode / Ralf Lehmann, 2023.
 *
 */

package uistate

data class SettingsUiState (
    val scheduleTime: Float = 15f,
    val executeOnlyOnWifi: Boolean = false,
    val includeQueryClients: Boolean = false,

    val ip: String = "",
    val username: String = "",
    val password: String = "",
    val queryPort: Int = 10011,
    val virtualServerId: Int = 1,

    val connectionSuccessful: Boolean? = null
)