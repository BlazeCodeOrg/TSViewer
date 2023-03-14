/*
 *
 *  * Copyright (c) BlazeCode / Ralf Lehmann, 2023.
 *
 */

package com.blazecode.tsviewer.database

import android.content.Context
import com.blazecode.tsviewer.data.TsClient
import com.blazecode.tsviewer.data.TsServerInfo
import com.blazecode.tsviewer.util.SettingsManager

class DatabaseManager(context: Context) {

    val clientRepository = ClientRepository(context)
    val serverRepository = ServerRepository(context)
    val settingsManager = SettingsManager(context)

    fun writeClients(list: MutableList<TsClient>){
           for (client in list) {
                val dbClient = clientRepository.getClientById(client.id)
                println(client.nickname)

                if (dbClient != null) {
                    clientRepository.insertClient(
                        client.copy(
                            activeConnectionTime = dbClient.activeConnectionTime + getScheduleTime().toInt()
                        )
                    )
                } else {
                    clientRepository.insertClient(client)
                }
           }
    }

    fun writeServerInfo(list: MutableList<TsClient>) {
        val serverInfo = TsServerInfo(
            timestamp = System.currentTimeMillis(),
            clients = list
        )
        serverRepository.insertServerInfo(serverInfo)
    }

    private fun getScheduleTime(): Float {
        return settingsManager.getScheduleTime()
    }
}