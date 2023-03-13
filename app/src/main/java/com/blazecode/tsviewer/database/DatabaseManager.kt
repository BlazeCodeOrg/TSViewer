/*
 *
 *  * Copyright (c) BlazeCode / Ralf Lehmann, 2023.
 *
 */

package com.blazecode.tsviewer.database

import android.content.Context
import com.blazecode.tsviewer.data.TsClient
import com.blazecode.tsviewer.util.SettingsManager

class DatabaseManager(context: Context) {

    val repository = ClientRepository(context)
    val settingsManager = SettingsManager(context)

    fun writeClients(list: MutableList<TsClient>){
           for (client in list) {
                val dbClient = repository.getClientById(client.id)
                println(client.nickname)

                if (dbClient != null) {
                    repository.insertClient(
                        client.copy(
                            activeConnectionTime = dbClient.activeConnectionTime + getScheduleTime().toInt()
                        )
                    )
                } else {
                    repository.insertClient(client)
                }
           }
    }

    fun getScheduleTime(): Float {
        return settingsManager.getScheduleTime()
    }
}