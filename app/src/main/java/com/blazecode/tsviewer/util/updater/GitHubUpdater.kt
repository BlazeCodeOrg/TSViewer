package com.blazecode.tsviewer.util.updater

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.beust.klaxon.Klaxon
import com.blazecode.tsviewer.BuildConfig
import com.blazecode.tsviewer.MainActivity
import com.blazecode.tsviewer.R
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import timber.log.Timber


class GitHubUpdater(private val context: Context) {

    fun checkForUpdate(){
        GlobalScope.launch {
            val queue = Volley.newRequestQueue(context)
            val url = "https://api.github.com/repos/BlazeCodeDev/TSViewer/releases"

            val stringRequest = StringRequest(
                Request.Method.GET, url,
                { response ->
                    Timber.tag("TSViewer").i("Got latest GitHub releases")
                    parseJSON(response)
                },
                {
                    Timber.tag("TSViewer").i("Error getting latest GitHub releases")
                })

            // Add the request to the RequestQueue.
            queue.add(stringRequest)
        }
    }

    private fun parseJSON(input: String){
        val releases = Klaxon().parseArray<GitHubRelease>(input)
        val latestReleaseVersion = releases?.get(0)?.tag_name?.removePrefix("V")

        if(BuildConfig.VERSION_NAME != latestReleaseVersion){
            Timber.tag("TSViewer").i("Found update")
            releases?.get(0)?.let { postNotification(it.tag_name, it.body) }
        } else {
            Timber.tag("TSViewer").i("No update available")
        }
    }

    private fun postNotification(releaseName: String, body: String) {
        val intent = Intent(context, MainActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        val clickIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE)

        val builder = NotificationCompat.Builder(context, context.getString(R.string.notificationChannelUpdateID))
            .setSmallIcon(R.drawable.ic_notification_icon)
            .setContentTitle(context.getString(R.string.update_available, releaseName))
            .setContentText(body)
            .setStyle(NotificationCompat.BigTextStyle()
                .bigText(body))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(clickIntent)

        with(NotificationManagerCompat.from(context)) {
            notify(2, builder.build())
        }
    }


    fun removeNotification(){
        NotificationManagerCompat.from(context).cancel(context.getString(R.string.notificationChannelUpdateID), 2)
    }

    fun createNotificationChannel(){
        val name = context.getString(R.string.notificationChannelUpdate)
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val channel = NotificationChannel(context.getString(R.string.notificationChannelUpdateID), name, importance).apply{}
        // Register the channel with the system
        val notificationManager: NotificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }
}