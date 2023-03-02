/*
 *
 *  * Copyright (c) BlazeCode / Ralf Lehmann, 2023.
 *
 */

package com.blazecode.tsviewer.util

import android.content.Context
import android.content.SharedPreferences
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import androidx.appcompat.app.AppCompatActivity
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.blazecode.tsviewer.R
import com.blazecode.tsviewer.uistate.SettingsUiState

class SettingsManager(val context: Context) {

    // INIT
    var preferences: SharedPreferences
    var encryptedSharedPreferences: SharedPreferences

    init {
        preferences = context.getSharedPreferences("preferences", AppCompatActivity.MODE_PRIVATE)
        encryptedSharedPreferences = EncryptedSharedPreferences.create(
            context,
            "encrypted_preferences",
            getMasterKey(context),
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM)
    }

    // PUBLIC FUNCTIONS
    fun getSettingsUiState(): SettingsUiState {
        return loadSettings()
    }

    fun saveSettingsUiState(uiState: SettingsUiState){
        val editor : SharedPreferences.Editor = preferences.edit()
        editor.putFloat("scheduleTime", uiState.scheduleTime)
        editor.putBoolean("includeQuery", uiState.includeQueryClients)
        editor.putBoolean("run_only_wifi", uiState.executeOnlyOnWifi)
        editor.apply()

        with (encryptedSharedPreferences.edit()) {
            this.putString("ip", uiState.ip)
            this.putString("user", uiState.username)
            this.putString("pass", uiState.password)
            this.putInt("queryport", uiState.queryPort)
            this.putInt("virtualServerId", uiState.virtualServerId)
            apply()
        }
    }

    // LOAD SETTINGS
    private fun loadSettings(): SettingsUiState {
        var tempUiState = SettingsUiState()

        tempUiState = tempUiState.copy(
            scheduleTime = preferences.getFloat("scheduleTime", 15f),
            ip = encryptedSharedPreferences.getString("ip", context.resources.getString(R.string.default_ip_address)).toString(),
            username = encryptedSharedPreferences.getString("user", context.resources.getString(R.string.default_query_user)).toString(),
            password = encryptedSharedPreferences.getString("pass", context.resources.getString(R.string.default_query_password)).toString(),
            queryPort = encryptedSharedPreferences.getInt("queryport", context.resources.getString(R.string.default_query_port).toInt()),
            virtualServerId = encryptedSharedPreferences.getInt("virtualServerId", context.resources.getString(R.string.default_virtual_server_id).toInt()),
            includeQueryClients = preferences.getBoolean("includeQuery", false),
            executeOnlyOnWifi = preferences.getBoolean("run_only_wifi", false)
        )

        return tempUiState
    }

    private fun getMasterKey(context: Context) : MasterKey {
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