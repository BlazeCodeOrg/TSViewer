/*
 *
 *  * Copyright (c) BlazeCode / Ralf Lehmann, 2023.
 *
 */

package com.blazecode.tsviewer.util

import android.content.Context
import android.content.SharedPreferences
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.provider.Settings
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import androidx.appcompat.app.AppCompatActivity
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.blazecode.tsviewer.R
import com.blazecode.tsviewer.data.ConnectionDetails
import com.blazecode.tsviewer.data.TsClient
import com.blazecode.tsviewer.database.DatabaseManager
import com.blazecode.tsviewer.util.database.UserCount
import com.blazecode.tsviewer.util.database.UserCountDAO
import com.blazecode.tsviewer.util.database.UserCountDatabase
import com.blazecode.tsviewer.util.notification.ClientNotificationManager
import com.blazecode.tsviewer.util.tile.TileManager
import com.blazecode.tsviewer.util.wear.WearDataManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


class ClientsWorker(private val context: Context, workerParameters: WorkerParameters) :
    CoroutineWorker(context, workerParameters) {

    val mContext = context

    val connectionManager = ConnectionManager(context)
    val clientNotificationManager = ClientNotificationManager(context)
    val tileManager = TileManager(context)
    val errorHandler = ErrorHandler(context)

    lateinit var db: UserCountDatabase
    lateinit var userCountDAO: UserCountDAO

    private var IP_ADRESS : String = ""
    private var USERNAME : String = ""
    private var PASSWORD : String = ""
    private var NICKNAME : String = "TSViewer"
    private var INCLUDE_QUERY_CLIENTS : Boolean = false
    private var PORT : Int = 0
    private var SERVER_ID : Int = 0
    private var RUN_ONLY_WIFI : Boolean = true
    private var DEMO_MODE : Boolean = false
    private var SYNC_WEARABLE : Boolean = false

    private var clientList = mutableListOf<TsClient>()
    private var clientListNames = mutableListOf<String>()

    private var wearDataManager = WearDataManager(context)

    override suspend fun doWork() : Result {
        loadPreferences()

        withContext(Dispatchers.IO){
            if((isWifi() && RUN_ONLY_WIFI) || !RUN_ONLY_WIFI) getClients()
            else {
                clientNotificationManager.removeNotification()
                writeClients(null)
            }
        }

        return Result.success()
    }

    private fun getClients(){
        clientList = connectionManager.getClients(ConnectionDetails(IP_ADRESS, USERNAME, PASSWORD, INCLUDE_QUERY_CLIENTS, PORT, SERVER_ID))

        if(SYNC_WEARABLE) wearDataManager.sendClientList(clientList)
        clientNotificationManager.post(clientList)
        writeClients(clientList)
    }

    private fun extractNames(){
        clientListNames.clear()
        for(client in clientList){
            clientListNames.add(client.nickname)
        }
    }

    private fun getLatestId() : Int {
        val latestEntry : UserCount? = userCountDAO.getLastEntry()
        return if(latestEntry == null) 0
        else latestEntry.id!!
    }

    private fun writeClients(list: MutableList<TsClient>?) {
        tileManager.init()
        if(list == null){
            run {
                var message : String = ""

                if(!isWifi() && RUN_ONLY_WIFI && hasCellReception()){
                    // ONLY WIFI ALLOWED, NO WIFI CONNECTION
                    message = context.getString(R.string.no_wifi)
                } else if (isAirplaneMode(context)){
                    // AIRPLANE MODE IS ACTIVE
                    message = context.getString(R.string.airplane_mode)
                } else {
                    // NO INTERNET CONNECTION
                    message = context.getString(R.string.no_network)
                }
                // QS TILE
                tileManager.error(message)
                // DATABASE
                val databaseManager = DatabaseManager(context)
                databaseManager.writeClients(mutableListOf())
            }
        } else {
            run {
                // QS TILE
                tileManager.post(clientListNames)
                // DATABASE
                val databaseManager = DatabaseManager(context)
                databaseManager.writeClients(list)
                databaseManager.writeServerInfo(list)
            }
        }
    }

    private fun isWifi() : Boolean{
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork ?: return false
        val activeNetwork = connectivityManager.getNetworkCapabilities(network) ?: return false
        return activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)
    }

    private fun hasCellReception() : Boolean{
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork ?: return false
        val activeNetwork = connectivityManager.getNetworkCapabilities(network) ?: return false
        return activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)
    }

    fun isAirplaneMode(context: Context): Boolean {
        return Settings.Global.getInt(
            context.contentResolver,
            Settings.Global.AIRPLANE_MODE_ON, 0
        ) != 0
    }

    private fun loadPreferences(){
        val preferences = context?.getSharedPreferences("preferences", AppCompatActivity.MODE_PRIVATE)!!
        //IP, USER AND PASS ARE ENCRYPTED
        NICKNAME = preferences.getString("nick", context.getString(R.string.app_name)).toString()
        INCLUDE_QUERY_CLIENTS = preferences.getBoolean("includeQuery", false)
        RUN_ONLY_WIFI = preferences.getBoolean("run_only_wifi", true)
        DEMO_MODE = preferences.getBoolean("demoMode", false)
        SYNC_WEARABLE = preferences.getBoolean("syncWearable", false)
        loadEncryptedPreferences()
    }

    private fun loadEncryptedPreferences(){
        val encryptedSharedPreferences: SharedPreferences = EncryptedSharedPreferences.create(
            context,
            "encrypted_preferences",
            getMasterKey(),
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM)

        IP_ADRESS = encryptedSharedPreferences.getString("ip", "").toString()
        USERNAME = encryptedSharedPreferences.getString("user", "").toString()
        PASSWORD = encryptedSharedPreferences.getString("pass", "").toString()
        PORT = encryptedSharedPreferences.getInt("queryport", context.getString(R.string.default_query_port).toInt())
        SERVER_ID = encryptedSharedPreferences.getInt("virtualServerId", context.getString(R.string.default_virtual_server_id).toInt())
    }

    private fun getMasterKey() : MasterKey {
        //MAKE AN ENCRYPTION KEY
        val spec = KeyGenParameterSpec.Builder("_androidx_security_master_key_",
            KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT)
            .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
            .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
            .setKeySize(256)
            .build()

        return MasterKey.Builder(context)
            .setKeyGenParameterSpec(spec)
            .build()
    }
}