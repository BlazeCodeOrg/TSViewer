/*
 *
 *  * Copyright (c) BlazeCode / Ralf Lehmann, 2023.
 *
 */

package com.blazecode.tsviewer.data

data class ConnectionDetails(
    val ip: String,
    val username: String,
    val password: String,
    val includeQueryClients: Boolean,
    val port: Int,
    val virtualServerId: Int
)
