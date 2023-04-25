/*
 *
 *  * Copyright (c) BlazeCode / Ralf Lehmann, 2023.
 *
 */

package com.blazecode.tsviewer.util

import com.blazecode.tsviewer.data.TsChannel
import com.blazecode.tsviewer.data.TsClient
import com.blazecode.tsviewer.data.TsServerInfo
import java.util.Date

object DemoModeValues {

    fun serverInfoList(): MutableList<TsServerInfo>{
        return mutableListOf(
            TsServerInfo(timestamp = 1620000000000, clients = mutableListOf(TsClient(nickname = "Cocktail"))),
            TsServerInfo(timestamp = 1620003600000, clients = mutableListOf(TsClient(nickname = "Cocktail"))),
            TsServerInfo(timestamp = 1620007200000, clients = mutableListOf(TsClient(nickname = "Cocktail"), TsClient(nickname = "Cosmo"))),
            TsServerInfo(timestamp = 1620010800000, clients = mutableListOf(TsClient(nickname = "Cocktail"), TsClient(nickname = "Cosmo"))),
            TsServerInfo(timestamp = 1620014400000, clients = mutableListOf(TsClient(nickname = "Cocktail"), TsClient(nickname = "Cosmo"), TsClient(nickname = "Dangle"))),
            TsServerInfo(timestamp = 1620018000000, clients = mutableListOf(TsClient(nickname = "Cocktail"), TsClient(nickname = "Cosmo"), TsClient(nickname = "Dangle"))),
            TsServerInfo(timestamp = 1620021600000, clients = mutableListOf(TsClient(nickname = "Cocktail"), TsClient(nickname = "Cosmo"), TsClient(nickname = "Dangle"))),
            TsServerInfo(timestamp = 1620025200000, clients = mutableListOf(TsClient(nickname = "Cocktail"), TsClient(nickname = "Cosmo"), TsClient(nickname = "Dangle"))),
            TsServerInfo(timestamp = 1620028800000, clients = mutableListOf(TsClient(nickname = "Cocktail"), TsClient(nickname = "Cosmo"))),
            TsServerInfo(timestamp = 1620032400000, clients = mutableListOf()),
        )
    }

    fun clientList(): MutableList<TsClient> {
        return mutableListOf(
            TsClient(nickname = "Cocktail", lastSeen = Date(1680512400000), activeConnectionTime = 480),
            TsClient(nickname = "Cosmo"),
            TsClient(nickname = "Dangle"),
            TsClient(nickname = "Commando"),
            TsClient(nickname = "SnoopWoot")
        )
    }

    fun channels(): MutableList<TsChannel> {
        return mutableListOf(
            TsChannel(name = "[cspacer]Rules"),
            TsChannel(name = "[*spacer1]_"),
            TsChannel(name = "[cspacer]Welcome"),
            TsChannel(name = "[*spacer2]_"),
            TsChannel(name = "Channel 1"),
            TsChannel(name = "Channel 2",
                members = mutableListOf(
                    TsClient(nickname = "Cocktail"),
                    TsClient(nickname = "Cosmo"),
                    TsClient(nickname = "Dangle", isInputMuted = true)
                )),
            TsChannel(name = "Channel 3",
                members = mutableListOf(
                    TsClient(nickname = "Commando"),
                    TsClient(nickname = "SnoopWoot")
                )),
            TsChannel(name = "Channel 4"),
            TsChannel(name = "Channel 5"),
            TsChannel(name = "[*spacer3]_"),
            TsChannel(name = "[cspacer]AFK"),
            TsChannel(name = "[*spacer4]_")
            )
    }
}