package com.blazecode.tsviewer.util.updater

import android.app.DownloadManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore.Files.FileColumns.MIME_TYPE
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.FileProvider
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.beust.klaxon.Klaxon
import com.blazecode.tsviewer.BuildConfig
import com.blazecode.tsviewer.MainActivity
import com.blazecode.tsviewer.R
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import timber.log.Timber
import java.io.File


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
            releases?.get(0)?.let { postNotification(releases[0]) }
        } else {
            Timber.tag("TSViewer").i("No update available")
        }
    }

    private fun postNotification(release: GitHubRelease) {
        val updateIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        updateIntent.putExtra("releaseName", release.tag_name)
        updateIntent.putExtra("releaseBody", release.body)
        updateIntent.putExtra("releaseLink", release.assets[0].browser_download_url)
        updateIntent.putExtra("releaseFileName", release.assets[0].name)
        val updatePendingIntent = PendingIntent.getActivity(
            context, 1, updateIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val builder = NotificationCompat.Builder(context, context.getString(R.string.notificationChannelUpdateID))
            .setGroup(context.getString(R.string.notificationChannelUpdateID))
            .setSmallIcon(R.drawable.ic_notification_icon)
            .setContentTitle(context.getString(R.string.update_available, release.tag_name))
            .setContentText(release.body)
            .setStyle(NotificationCompat.BigTextStyle()
                .bigText(release.body))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(updatePendingIntent)

        with(NotificationManagerCompat.from(context)) {
            notify(2, builder.build())
        }
    }

    fun downloadDialog(releaseName: String, releaseBody: String, releaseLink: String, releaseFileName: String){
        removeNotification()
        MaterialAlertDialogBuilder(context)
            .setTitle(context.getString(R.string.update_available, releaseName))
            .setMessage(releaseBody)
            .setPositiveButton(context.getString(R.string.download)) {dialog, which ->
                downloadAndInstall(Uri.parse(releaseLink), releaseFileName)
            }
            .setNegativeButton(context.getString(R.string.cancel)) {dialog, which ->
                dialog.dismiss()
            }
            .show()
    }

    fun downloadAndInstall(link: Uri, fileName: String){
        val downloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        val downloadUri = link
        val request = DownloadManager.Request(downloadUri)
        request.setMimeType(MIME_TYPE)
        val destination = context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS).toString() + "/" + fileName
        val destinationUri = Uri.parse("file://$destination")
        request.setDestinationUri(destinationUri)

        fun showInstallOption(destination: String) {
            val onComplete = object : BroadcastReceiver() {
                override fun onReceive(
                    context: Context,
                    intent: Intent
                ) {
                    val contentUri = FileProvider.getUriForFile(
                        context,
                        BuildConfig.APPLICATION_ID + ".provider",
                        File(destination)
                    )

                    val install = Intent(Intent.ACTION_VIEW)
                    install.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    install.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    install.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    install.putExtra(Intent.EXTRA_NOT_UNKNOWN_SOURCE, true)
                    install.data = contentUri
                    context.startActivity(install)
                    context.unregisterReceiver(this)

                    Toast.makeText(context, "done", Toast.LENGTH_LONG).show()
                }
            }
            context.registerReceiver(onComplete, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))
        }

        showInstallOption(destination)
        downloadManager.enqueue(request)
        Toast.makeText(context, context.getString(R.string.downloading), Toast.LENGTH_LONG).show()
    }

    private fun removeNotification(){
        NotificationManagerCompat.from(context).cancel(null, 2)
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