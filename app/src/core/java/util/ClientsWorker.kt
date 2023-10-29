/*
 *
 *  * Copyright (c) BlazeCode / Ralf Lehmann, 2023.
 *
 */

package util

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
import com.blazecode.tsviewer.data.ErrorCode
import com.blazecode.tsviewer.data.TsClient
import com.blazecode.tsviewer.database.DatabaseManager
import com.blazecode.tsviewer.util.ConnectionManager
import com.blazecode.tsviewer.util.ErrorHandler
import com.blazecode.tsviewer.util.notification.ClientNotificationManager
import com.blazecode.tsviewer.util.tile.TileManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import wear.WearDataManager


class ClientsWorker(private val context: Context, workerParameters: WorkerParameters) :
    CoroutineWorker(context, workerParameters) {

    val mContext = context

    val connectionManager = ConnectionManager(context)
    val clientNotificationManager = ClientNotificationManager(context)
    val tileManager = TileManager(context)
    val errorHandler = ErrorHandler(context)

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

        val suppress_DB = inputData.getBoolean("suppress_db", false)
        val suppress_Notification = inputData.getBoolean("suppress_notification", false)
        val suppress_Wearable = inputData.getBoolean("suppress_wearable", false)

        withContext(Dispatchers.IO){
            if((isWifi() && RUN_ONLY_WIFI) || !RUN_ONLY_WIFI) {
                getClients(
                    postNotification = !suppress_Notification,
                    writeDB = !suppress_DB,
                    syncWearable = !suppress_Wearable
                )
            } else {
                clientNotificationManager.removeNotification()
                writeClients(mutableListOf(), ErrorCode.NO_WIFI)
            }
        }

        return Result.success()
    }

    private fun getClients(
        postNotification: Boolean = true,
        writeDB: Boolean = true,
        syncWearable: Boolean = true
    ){
        clientList = connectionManager.getClients(ConnectionDetails(IP_ADRESS, USERNAME, PASSWORD, INCLUDE_QUERY_CLIENTS, PORT, SERVER_ID))

        var code : ErrorCode = ErrorCode.NO_ERROR
        if (clientList.isEmpty()) {
            if(!isWifi() && RUN_ONLY_WIFI && hasCellReception()){
                // ONLY WIFI ALLOWED, NO WIFI CONNECTION
                code = ErrorCode.NO_WIFI
            } else if (isAirplaneMode(context)){
                // AIRPLANE MODE IS ACTIVE
                code = ErrorCode.AIRPLANE_MODE
            } else {
                // NO INTERNET CONNECTION
                code = ErrorCode.NO_NETWORK
            }
        }
        if (code != ErrorCode.NO_ERROR) tileManager.error(code)

        if(syncWearable && SYNC_WEARABLE)
            wearDataManager.sendClientList(clientList, code)
        if(postNotification)
            clientNotificationManager.post(clientList)
        if(writeDB)
            writeClients(clientList, code)
    }

    private fun writeClients(list: MutableList<TsClient>, errorCode: ErrorCode) {
        if(list.size == 0 && errorCode != ErrorCode.NO_ERROR){
            run {
                val databaseManager = DatabaseManager(context)
                databaseManager.writeClients(mutableListOf())
            }
        } else {
            run {
                // QS TILE
                tileManager.post(list)
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