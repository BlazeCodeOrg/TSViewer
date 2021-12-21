package com.blazecode.tsviewer.util.tile

import android.content.Intent
import android.os.Build
import android.service.quicksettings.Tile
import android.service.quicksettings.TileService
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.blazecode.tsviewer.MainActivity


class ClientTileService : TileService() {

    override fun onClick() {
        super.onClick()

        startActivityAndCollapse(Intent(applicationContext, MainActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onStartListening() {
        super.onStartListening()

        //GET SHARED PREFERENCES
        val tileSetting = this.getSharedPreferences("tile", AppCompatActivity.MODE_PRIVATE)!!
        val tileActive = tileSetting.getBoolean("stateActive", false)

        //SET STATE
        if(tileActive) qsTile.state = Tile.STATE_ACTIVE
        else qsTile.state = Tile.STATE_INACTIVE

        //SET SUBTITLE
        qsTile.subtitle = tileSetting.getString("subtitle", "")

        //APPLY
        qsTile.updateTile()
    }

    override fun onTileAdded() {
        super.onTileAdded()

        //ENABLE TILE IF DEVICE IS NEWER OR EQUAL ANDROID 10
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            qsTile.state = Tile.STATE_INACTIVE
            qsTile.updateTile()
        } else {
            qsTile.label = "Not supported"
            qsTile.updateTile()
        }
    }
}