package com.blazecode.tsviewer.util

import android.widget.Toast
import com.blazecode.tsviewer.enums.ConnectionStatus
import com.github.theholywaffle.teamspeak3.TS3Api
import com.github.theholywaffle.teamspeak3.TS3Config
import com.github.theholywaffle.teamspeak3.TS3Query
import com.github.theholywaffle.teamspeak3.api.wrapper.Client
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.lang.Exception

object ConnectionManager {

    var lastRandom : Int = 0

    fun getClients(ip : String, username : String, password : String, nickname : String, randomizedNickname: Boolean) : MutableList<Client> {
        var clientList = mutableListOf<Client>()
        try {
            GlobalScope.launch(Dispatchers.IO){
                var config = TS3Config()
                config.setHost(ip)
                config.setFloodRate(TS3Query.FloodRate.UNLIMITED)
                config.setEnableCommunicationsLogging(true)

                val query = TS3Query(config)

                query.connect()
                val api = query.api
                api.login(username, password)
                api.selectVirtualServerById(1)
                if(randomizedNickname) api.setNickname(randomizedNickname(nickname))
                else api.setNickname(nickname)
                api.sendPrivateMessage(24,"TSViewer is online")

                for (client : Client in api.clients) {
                    clientList.add(client)
                }

                query.exit()
            }
        } catch (e : Exception){
            return mutableListOf()
        }
        return clientList
    }

    private fun randomizedNickname(nickname: String): String {
        var random = (0..100000).random()
        while(random == lastRandom) random = (0..100000).random()                   //MAKE SURE THAT IT DOESNT USE THE SAME NUMBER TWICE IN A ROW
        lastRandom = random
        return "$nickname$random"
    }
}