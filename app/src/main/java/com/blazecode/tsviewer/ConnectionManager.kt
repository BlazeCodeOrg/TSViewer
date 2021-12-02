package com.blazecode.tsviewer

import android.widget.Toast
import com.blazecode.tsviewer.enums.ConnectionStatus
import com.github.theholywaffle.teamspeak3.TS3Api
import com.github.theholywaffle.teamspeak3.TS3Config
import com.github.theholywaffle.teamspeak3.TS3Query
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.lang.Exception

object ConnectionManager {

    fun connect(ip : String, username : String, password : String, nickname : String) : ConnectionStatus{
        try {
            GlobalScope.launch(Dispatchers.IO){
                val config = TS3Config()
                config.setHost(ip)
                config.setEnableCommunicationsLogging(true)

                val query = TS3Query(config)
                query.connect()

                val api = query.api
                api.login(username, password)
                api.selectVirtualServerById(1)
                api.setNickname(nickname)
                api.sendPrivateMessage(29, "TSViewer is online")
            }
        } catch (e : Exception){
            return ConnectionStatus.ERROR
        }
        return ConnectionStatus.CONNECTED
    }

    fun disconnect(){

    }
}