/*
 *
 *  * Copyright (c) BlazeCode / Ralf Lehmann, 2023.
 *
 */

package com.blazecode.tsviewer.wear.communication

import android.util.Log
import com.google.android.gms.wearable.MessageClient
import com.google.android.gms.wearable.MessageEvent

class WearDataManager() :
    MessageClient.OnMessageReceivedListener{

    override fun onMessageReceived(messageEvent: MessageEvent) {
        Log.d("communication","Got message: ${messageEvent.path}")
    }
}