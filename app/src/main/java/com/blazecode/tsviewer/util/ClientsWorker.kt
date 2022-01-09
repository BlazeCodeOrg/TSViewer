package com.blazecode.tsviewer.util

import android.content.Context
import android.content.SharedPreferences
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Looper
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LifecycleOwner
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import androidx.work.*
import com.blazecode.tsviewer.R
import com.blazecode.tsviewer.util.notification.NotificationManager
import com.blazecode.tsviewer.util.tile.TileManager
import com.github.theholywaffle.teamspeak3.api.wrapper.Client
import java.util.*
import kotlin.coroutines.coroutineContext

class ClientsWorker(private val context: Context, workerParameters: WorkerParameters) :
    Worker(context, workerParameters) {

    val connectionManager = ConnectionManager(context)
    val notificationManager = NotificationManager(context)
    val tileManager = TileManager(context)
    val errorHandler = ErrorHandler(context)

    private var IP_ADRESS : String = ""
    private var USERNAME : String = ""
    private var PASSWORD : String = ""
    private var NICKNAME : String = "TSViewer"
    private var RANDOMIZE_NICKNAME : Boolean = true
    private var INCLUDE_QUERY_CLIENTS : Boolean = false
    private var RUN_ONLY_WIFI : Boolean = true

    private var clientList = mutableListOf<Client>()
    private var clientListNames = mutableListOf<String>()


    override fun doWork() : Result {
        loadPreferences()
        if((isWifi() && RUN_ONLY_WIFI) || !RUN_ONLY_WIFI) getClients()
        else {
            notificationManager.removeNotification()
            tileManager.init()
            tileManager.noNetwork()
        }

        return Result.success()
    }

    private fun getClients(){
        clientList = connectionManager.getClients(IP_ADRESS, USERNAME, PASSWORD, NICKNAME, RANDOMIZE_NICKNAME, INCLUDE_QUERY_CLIENTS)
        extractNames()

        notificationManager.post(clientListNames)
        tileManager.init()
        tileManager.post(clientListNames)

        errorHandler.reportError("Got ${clientList.size} client/s: ${clientListNames.joinToString()}")
    }

    private fun extractNames(){
        clientListNames.clear()
        for(client in clientList){
            clientListNames.add(client.nickname)
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
        RANDOMIZE_NICKNAME = preferences.getBoolean("randNick", true)
        INCLUDE_QUERY_CLIENTS = preferences.getBoolean("includeQuery", false)
        RUN_ONLY_WIFI = preferences.getBoolean("run_only_wifi", true)
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