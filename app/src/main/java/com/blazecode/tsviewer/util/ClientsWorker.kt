/*
 *
 *  * Copyright (c) BlazeCode / Ralf Lehmann, 2022.
 *
 */

package com.blazecode.tsviewer.util

import android.content.Context
import android.content.SharedPreferences
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import androidx.appcompat.app.AppCompatActivity
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.blazecode.tsviewer.R
import com.blazecode.tsviewer.util.database.UserCount
import com.blazecode.tsviewer.util.database.UserCountDAO
import com.blazecode.tsviewer.util.database.UserCountDatabase
import com.blazecode.tsviewer.util.notification.ClientNotificationManager
import com.blazecode.tsviewer.util.tile.TileManager
import com.github.theholywaffle.teamspeak3.api.wrapper.Client

class ClientsWorker(private val context: Context, workerParameters: WorkerParameters) :
    Worker(context, workerParameters) {

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
    private var RUN_ONLY_WIFI : Boolean = true
    private var DEMO_MODE : Boolean = false

    private var clientList = mutableListOf<Client>()
    private var clientListNames = mutableListOf<String>()


    override fun doWork() : Result {
        loadPreferences()

        // OPEN DB
        db = UserCountDatabase.build(mContext)
        userCountDAO = db.userCountDao()

        if((isWifi() && RUN_ONLY_WIFI) || !RUN_ONLY_WIFI) getClients()
        else {
            clientNotificationManager.removeNotification()
            tileManager.init()
            tileManager.noNetwork()
            saveToDatabase(null)
        }

        return Result.success()
    }

    private fun getClients(){
        clientList = connectionManager.getClients(IP_ADRESS, USERNAME, PASSWORD, NICKNAME, getLatestId(), INCLUDE_QUERY_CLIENTS)
        if(DEMO_MODE)
            clientListNames = mutableListOf("Cocktail", "Cosmo", "Commando", "Dangle", "SnoopWoot")
        else
            extractNames()

        clientNotificationManager.post(clientListNames)
        tileManager.init()
        tileManager.post(clientListNames)
        saveToDatabase(clientListNames)
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

    private fun saveToDatabase(list: MutableList<String>?) {
        if(list == null){
            run {
                val userCount = UserCount(null, System.currentTimeMillis(), 0, context.getString(R.string.no_network))
                userCountDAO.insertUserCount(userCount)
            }
        } else {
            run {
                val userCount = UserCount(null, System.currentTimeMillis(), list.size, list.joinToString())
                userCountDAO.insertUserCount(userCount)
            }
        }
    }

    private fun isWifi() : Boolean{
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork ?: return false
        val activeNetwork = connectivityManager.getNetworkCapabilities(network) ?: return false
        return activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)
    }

    private fun loadPreferences(){
        val preferences = context?.getSharedPreferences("preferences", AppCompatActivity.MODE_PRIVATE)!!
        //IP, USER AND PASS ARE ENCRYPTED
        NICKNAME = preferences.getString("nick", context.getString(R.string.app_name)).toString()
        INCLUDE_QUERY_CLIENTS = preferences.getBoolean("includeQuery", false)
        RUN_ONLY_WIFI = preferences.getBoolean("run_only_wifi", true)
        DEMO_MODE = preferences.getBoolean("demoMode", false)
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