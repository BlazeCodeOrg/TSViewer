/*
 *
 *  * Copyright (c) BlazeCode / Ralf Lehmann, 2023.
 *
 */

package com.blazecode.tsviewer.util

import android.content.Context
import com.github.theholywaffle.teamspeak3.TS3Config
import com.github.theholywaffle.teamspeak3.TS3Query
import com.github.theholywaffle.teamspeak3.api.wrapper.Client
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class ConnectionManager(val context: Context) {

    var lastRandom : Int = 0
    var apiNickname : String = ""

    val errorHandler = ErrorHandler(context)

    fun getClients(ip : String, username : String, password : String, nickname : String, id: Int, includeQueryClients: Boolean, port: Int, virtualServerId: Int) : MutableList<Client> {
        var clientList = mutableListOf<Client>()

        runBlocking {
            val apiCall = GlobalScope.launch(exceptionHandler){
                //CONFIGURE
                var config = TS3Config()
                config.setHost(ip)
                config.setFloodRate(TS3Query.FloodRate.UNLIMITED)
                config.setEnableCommunicationsLogging(true)
                config.setQueryPort(port)

                //CONNECT QUERY
                val query = TS3Query(config)
                query.connect()
                val api = query.api
                api.login(username, password)
                api.selectVirtualServerById(virtualServerId)
                api.setNickname("$nickname$id")

                //GET CLIENTS
                clientList = api.clients
                clientList = filterClients(clientList, includeQueryClients, apiNickname)

                //DISCONNECT AFTER TASK
                query.exit()
            }
            apiCall.join()
        }
        return clientList
    }

    fun testConnection(ip : String, username : String, password : String, queryPort : Int, virtualServerId: Int): Boolean {
        var connectionSuccessful = false

        runBlocking {
            val apiCall = GlobalScope.launch(exceptionHandler){
                //CONFIGURE
                var config = TS3Config()
                config.setHost(ip)
                config.setFloodRate(TS3Query.FloodRate.UNLIMITED)
                config.setEnableCommunicationsLogging(true)
                config.setQueryPort(queryPort)

                //CONNECT QUERY
                val query = TS3Query(config)
                query.connect()
                val api = query.api
                api.login(username, password)
                api.selectVirtualServerById(virtualServerId)

                //DISCONNECT AFTER TASK
                query.exit()
                connectionSuccessful = true
            }
            apiCall.join()
        }
        return connectionSuccessful
    }

    private val exceptionHandler = CoroutineExceptionHandler { _, exception ->
        errorHandler.reportError(exception.toString())
    }

    private fun filterClients(clientList: MutableList<Client>, includeQueryClients: Boolean, nickname: String): MutableList<Client> {
        var tempList = mutableListOf<Client>()
        for (client in clientList){
            if(!includeQueryClients && client.platform != "ServerQuery") tempList.add(client)               //FILTER OUT ALL QUERY CLIENTS
            else if(includeQueryClients && client.nickname != nickname) tempList.add(client)                //FILTER OUT ONLY THE CLIENT CREATED BY THIS APP
        }
        return tempList
    }
}