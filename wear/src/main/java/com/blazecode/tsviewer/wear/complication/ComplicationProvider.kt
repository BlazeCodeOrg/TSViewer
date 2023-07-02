/*
 *
 *  * Copyright (c) BlazeCode / Ralf Lehmann, 2022.
 *
 */

package com.blazecode.tsviewer.wear.complication

import android.app.PendingIntent
import android.app.TaskStackBuilder
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.graphics.drawable.Icon
import androidx.wear.watchface.complications.data.*
import androidx.wear.watchface.complications.datasource.ComplicationDataSourceService
import androidx.wear.watchface.complications.datasource.ComplicationDataSourceUpdateRequester
import androidx.wear.watchface.complications.datasource.ComplicationRequest
import com.blazecode.tsviewer.R
import com.blazecode.tsviewer.wear.MainActivity
import data.DataHolder

class ComplicationProvider: ComplicationDataSourceService() {

    val dataHolder = DataHolder

    override fun getPreviewData(type: ComplicationType): ComplicationData? {
        return getComplicationData(null)
    }

    override fun onComplicationRequest(request: ComplicationRequest, listener: ComplicationRequestListener) {

        val tapIntent = Intent(this, MainActivity::class.java)
            .putExtra("openClientScreen", true)
        val tapPendingIntent: PendingIntent? = TaskStackBuilder.create(this).run {
            addNextIntentWithParentStack(tapIntent)
            getPendingIntent(dataHolder.list.size, PendingIntent.FLAG_IMMUTABLE)
        }

        listener.onComplicationData(getComplicationData(tapPendingIntent))
    }

    private fun getComplicationData(tapAction: PendingIntent?): ComplicationData{
        val icon = Icon.createWithResource(this, R.drawable.ic_icon)

        return ShortTextComplicationData.Builder(
            text = PlainComplicationText.Builder(
                text = dataHolder.list.size.toString()
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
