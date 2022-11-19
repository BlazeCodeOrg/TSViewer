/*
 *
 *  * Copyright (c) BlazeCode / Ralf Lehmann, 2022.
 *
 */

package com.blazecode.tsviewer.wear.complication

import android.app.PendingIntent
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.graphics.drawable.Icon
import androidx.wear.watchface.complications.data.*
import androidx.wear.watchface.complications.datasource.ComplicationDataSourceService
import androidx.wear.watchface.complications.datasource.ComplicationDataSourceUpdateRequester
import androidx.wear.watchface.complications.datasource.ComplicationRequest
import com.blazecode.tsviewer.R


// https://github.com/android/wear-os-samples/blob/main/WearComplicationDataSourcesTestSuite/Wearable/src/main/java/com/example/android/wearable/wear/wearcomplicationproviderstestsuite/LongTextDataSourceService.kt

class ComplicationProvider: ComplicationDataSourceService() {

    val dataHolder = ComplicationDataHolder

    override fun getPreviewData(type: ComplicationType): ComplicationData? {
        return getComplicationData(null)
    }

    override fun onComplicationRequest(request: ComplicationRequest, listener: ComplicationRequestListener) {
        val intent = Intent(this, ComplicationReceiver::class.java).apply {
            putExtra("amount", dataHolder.amount)
        }
        val pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)

        listener.onComplicationData(getComplicationData(pendingIntent))
    }

    private fun getComplicationData(tapAction: PendingIntent?): ComplicationData{
        val icon = Icon.createWithResource(this, R.drawable.ic_icon)

        return ShortTextComplicationData.Builder(
            text = PlainComplicationText.Builder(
                text = dataHolder.amount.toString()
            ).build(),

            contentDescription = PlainComplicationText.Builder(
                text = "Shows client amount"
            ).build(),
        )
            .setMonochromaticImage(MonochromaticImage.Builder(icon).build())
            .setTapAction(tapAction).build()
    }

    fun update(context: Context){
        ComplicationDataSourceUpdateRequester.create(
            context,
            complicationDataSourceComponent = ComponentName(context, ComplicationProvider::class.java)
        ).requestUpdateAll()
    }
}
