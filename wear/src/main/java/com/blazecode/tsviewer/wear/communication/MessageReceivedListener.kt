/*
 *
 *  * Copyright (c) BlazeCode / Ralf Lehmann, 2022.
 *
 */

package com.blazecode.tsviewer.wear.communication

import android.content.Context
import android.util.Log
import com.blazecode.tsviewer.wear.complication.ComplicationDataHolder
import com.blazecode.tsviewer.wear.complication.ComplicationProvider
import com.google.android.gms.wearable.MessageClient
import com.google.android.gms.wearable.MessageEvent

class MessageReceivedListener(val context: Context): MessageClient.OnMessageReceivedListener {

    override fun onMessageReceived(messageEvent: MessageEvent) {
        Log.d("messages", messageEvent.toString())
        ComplicationDataHolder.list = mutableListOf(messageEvent.toString())
        ComplicationProvider().update(context)
    }
}