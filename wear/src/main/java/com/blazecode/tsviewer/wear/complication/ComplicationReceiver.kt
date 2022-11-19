/*
 *
 *  * Copyright (c) BlazeCode / Ralf Lehmann, 2022.
 *
 */

package com.blazecode.tsviewer.wear.complication

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

class ComplicationReceiver: BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        val amount = intent?.extras?.getInt("amount")
        Log.d("test", amount.toString())
    }
}