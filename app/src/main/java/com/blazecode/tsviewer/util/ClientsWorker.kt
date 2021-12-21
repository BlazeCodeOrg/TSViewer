package com.blazecode.tsviewer.util

import android.content.Context
import android.service.quicksettings.Tile
import androidx.appcompat.app.AppCompatActivity
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.blazecode.tsviewer.R
import com.blazecode.tsviewer.util.notification.NotificationManager
import com.blazecode.tsviewer.util.tile.ClientTileService
import com.blazecode.tsviewer.util.tile.TileManager
import com.github.theholywaffle.teamspeak3.api.wrapper.Client

class ClientsWorker(private val context: Context, workerParameters: WorkerParameters) :
    Worker(context, workerParameters) {

    val connectionManager = ConnectionManager
    val notificationManager = NotificationManager(context)
    val tileManager = TileManager(context)

    private var IP_ADRESS : String = ""
    private var USERNAME : String = ""
    private var PASSWORD : String = ""
    private var NICKNAME : String = "TSViewer"
    private var RANDOMIZE_NICKNAME : Boolean = true
    private var INCLUDE_QUERY_CLIENTS : Boolean = false

    private var clientList = mutableListOf<Client>()
    private var clientListNames = mutableListOf<String>()

    override fun doWork() : Result {
        loadPreferences()
        getClients()

        return Result.success()
    }

    private fun getClients(){
        clientList = connectionManager.getClients(IP_ADRESS, USERNAME, PASSWORD, NICKNAME, RANDOMIZE_NICKNAME, INCLUDE_QUERY_CLIENTS)
        extractNames()

        notificationManager.post(clientListNames)
        tileManager.init()
        tileManager.post(clientListNames)
    }

    private fun extractNames(){
        clientListNames.clear()
        for(client in clientList){
            clientListNames.add(client.nickname)
        }
    }

    private fun loadPreferences(){
        val preferences = context?.getSharedPreferences("preferences", AppCompatActivity.MODE_PRIVATE)!!
        IP_ADRESS = preferences.getString("ip", "").toString()
        USERNAME = preferences.getString("user", "").toString()
        PASSWORD = preferences.getString("pass", "").toString()
        NICKNAME = preferences.getString("nick", context.getString(R.string.app_name)).toString()
        RANDOMIZE_NICKNAME = preferences.getBoolean("randNick", true)
        INCLUDE_QUERY_CLIENTS = preferences.getBoolean("includeQuery", false)
    }
}