package com.blazecode.tsviewer.util

import android.util.Log
import com.github.theholywaffle.teamspeak3.TS3Config
import com.github.theholywaffle.teamspeak3.TS3Query
import com.github.theholywaffle.teamspeak3.api.wrapper.Client
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

object ConnectionManager {

    var lastRandom : Int = 0
    var apiNickname : String = ""

    val errorHandler = ErrorHandler()

    fun getClients(ip : String, username : String, password : String, nickname : String, randomizedNickname: Boolean, includeQueryClients: Boolean) : MutableList<Client> {
        var clientList = mutableListOf<Client>()

        runBlocking {
            val apiCall = GlobalScope.launch(Dispatchers.IO){
                //CONFIGURE
                var config = TS3Config()
                config.setHost(ip)
                config.setFloodRate(TS3Query.FloodRate.UNLIMITED)
                config.setEnableCommunicationsLogging(true)

                //CONNECT QUERY
                val query = TS3Query(config)
                query.connect()
                val api = query.api
                api.login(username, password)
                api.selectVirtualServerById(1)
                if(randomizedNickname){
                    apiNickname = randomizedNickname(nickname)
                    api.setNickname(apiNickname)
                } else {
                    apiNickname = nickname
                    api.setNickname(nickname)
                }

                //NOTIFY CLIENT THAT APP HAS GRABBED INFO
                try {
                    val clientId = api.getClientByNameExact("TimeLabsmedia", true).id
                    api.sendPrivateMessage(clientId,"Grabbed Clients")
                } catch (e: Exception){
                    errorHandler.reportError(e, "Could not notify client")
                }


                //GET CLIENTS
                clientList = api.clients
                clientList = filterClients(clientList, includeQueryClients, apiNickname)

                //DISCONNECT AFTER TASK
                query.exit()
            }
            apiCall.join()
            Log.i("coroutine", "finished coroutine")
        }
        Log.i("coroutine", "returned list")
        return clientList
    }

    private fun randomizedNickname(nickname: String): String {
        var random = (0..100000).random()
        while(random == lastRandom) random = (0..100000).random()                                           //MAKE SURE THAT IT DOESN'T USE THE SAME NUMBER TWICE IN A ROW
        lastRandom = random
        return "$nickname$random"
    }


    private fun filterClients(clientList: MutableList<Client>, includeQueryClients: Boolean, nickname: String): MutableList<Client> {
        var tempList = mutableListOf<Client>()
        for (client in clientList){
            if(!includeQueryClients && client.platform != "ServerQuery") tempList.add(client)
            else if(includeQueryClients && client.nickname != nickname) tempList.add(client)
        }
        return tempList
    }
}