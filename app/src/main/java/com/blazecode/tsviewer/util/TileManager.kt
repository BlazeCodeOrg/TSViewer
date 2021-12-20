package com.blazecode.tsviewer.util

import android.service.quicksettings.Tile
import android.service.quicksettings.TileService
import com.blazecode.tsviewer.R

class TileManager : TileService() {

    fun post(clientListNames: MutableList<String>) {
        val tile = qsTile
        tile.contentDescription
        if (clientListNames.size == 1) tile.contentDescription = ("${clientListNames.size} ${resources.getString(R.string.client_connected)}")
        else tile.contentDescription = ("${clientListNames.size} ${resources.getString(R.string.clients_connected)}")

        tile.updateTile()
    }

    fun setState(state: Boolean) {
        val tile = qsTile
        if(state) tile.state = Tile.STATE_ACTIVE
        else tile.state = Tile.STATE_INACTIVE
        tile.updateTile()
    }

    override fun onClick() {
        super.onClick()

        // Called when the user click the tile
    }

    override fun onTileAdded() {
        super.onTileAdded()

        qsTile.state = Tile.STATE_INACTIVE
        qsTile.updateTile()
    }
}