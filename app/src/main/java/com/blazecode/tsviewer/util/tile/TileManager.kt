package com.blazecode.tsviewer.util.tile

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import com.blazecode.tsviewer.R

class TileManager(val context: Context) {

    lateinit var tileSettings : SharedPreferences
    lateinit var editor : SharedPreferences.Editor

    fun init(){
        //INITIALIZE SHARED PREFERENCES
        tileSettings = context?.getSharedPreferences("tile", AppCompatActivity.MODE_PRIVATE)!!
        editor = tileSettings.edit()
    }

    fun post(clientListNames: MutableList<String>) {
        //DISABLE TILE WHEN SERVER IS EMPTY
        if(clientListNames.isEmpty()) setState(false)
        else setState(true)

        //SAVE SUBTITLE
        if (clientListNames.size == 1) setSubtitle("${clientListNames.size} ${context.getString(R.string.client)}")
        else setSubtitle("${clientListNames.size} ${context.getString(R.string.clients)}")
        editor.commit()
    }

    fun noNetwork(){
        setState(false)
        setSubtitle("${context.getString(R.string.no_network)}")
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