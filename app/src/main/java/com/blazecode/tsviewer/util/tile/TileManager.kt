/*
 *
 *  * Copyright (c) BlazeCode / Ralf Lehmann, 2023.
 *
 */

package com.blazecode.tsviewer.util.tile

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import com.blazecode.tsviewer.R
import com.blazecode.tsviewer.data.ErrorCode
import com.blazecode.tsviewer.data.TsClient

class TileManager(val context: Context) {

    lateinit var tileSettings : SharedPreferences
    lateinit var editor : SharedPreferences.Editor

    init {
        //INITIALIZE SHARED PREFERENCES
        tileSettings = context?.getSharedPreferences("tile", AppCompatActivity.MODE_PRIVATE)!!
        editor = tileSettings.edit()
    }

    fun post(clientListNames: MutableList<TsClient>) {
        //DISABLE TILE WHEN SERVER IS EMPTY
        if(clientListNames.isEmpty()) setState(false)
        else setState(true)

        //SAVE SUBTITLE
        if (clientListNames.size == 1) setSubtitle("${clientListNames.size} ${context.getString(R.string.client)}")
        else setSubtitle("${clientListNames.size} ${context.getString(R.string.clients)}")
        editor.commit()
    }

    fun error (code : ErrorCode){
        setState(false)
        when (code) {
            ErrorCode.NO_ERROR -> setSubtitle("")
            ErrorCode.NO_NETWORK -> setSubtitle(context.getString(R.string.no_network))
            ErrorCode.AIRPLANE_MODE -> setSubtitle(context.getString(R.string.airplane_mode))
            ErrorCode.NO_WIFI -> setSubtitle(context.getString(R.string.no_wifi))
        }
    }

    fun setSubtitle(subtitle: String){
        editor.putString("subtitle", subtitle)
        editor.commit()
    }

    fun setState(state: Boolean) {
        editor.putBoolean("stateActive", state)
        editor.commit()
    }
}