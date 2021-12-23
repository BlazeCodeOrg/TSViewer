package com.blazecode.tsviewer

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.res.Resources
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.AttributeSet
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import com.blazecode.tsviewer.databinding.ActivityMainBinding
import com.blazecode.tsviewer.util.notification.NotificationManager
import com.mikepenz.aboutlibraries.Libs
import com.mikepenz.aboutlibraries.LibsBuilder
import kotlinx.coroutines.NonCancellable.start
import java.util.*


class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.toolbar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.action_source -> {
                    openLink(getString(R.string.github_source_url))
                    return@setOnMenuItemClickListener true
                }

                R.id.action_licenses -> {
                    LibsBuilder()
                        .withLicenseShown(true)
                        .withAboutIconShown(true)
                        .withVersionShown(true)
                        .withActivityTitle(getString(R.string.licenses))
                        .withAboutDescription("test description")
                        .start(this)

                    return@setOnMenuItemClickListener true
                }

                R.id.action_send_email -> {
                    sendMail("Report")
                    return@setOnMenuItemClickListener true
                }
                else -> false
            }
        }

        //CREATE NOTIFICATION CHANNEL IF FIRST START
        if (isFirstStart()){
            val notificationManager = NotificationManager(this)
            notificationManager.createChannel()
        }
    }

    private fun sendMail(subject: String) {
        val intent = Intent(Intent.ACTION_SENDTO)
        intent.data = Uri.parse("mailto:")
        intent.putExtra(Intent.EXTRA_EMAIL, arrayOf(getString(R.string.email_address)))
        intent.putExtra(Intent.EXTRA_SUBJECT, subject + " - " + getString(R.string.app_name))

        intent.putExtra(
            Intent.EXTRA_TEXT,
            "App Version: ${BuildConfig.VERSION_NAME}" +
                    "\nAndroid Version: ${Build.VERSION.SDK_INT}" +
                    "\nDeviceInfo: ${Build.MANUFACTURER} ${Build.MODEL}" +
                    "\nDeviceLanguage: ${Resources.getSystem().configuration.locale.language}" +
                    "\n\nPlease consider attaching a screenshot or recording." +
                    "\nPlease describe your issue below this line.\n\n"
        )

        startActivity(Intent.createChooser(intent, getString(R.string.send_email)))
    }

    private fun openLink (url: String) {
        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        startActivity(browserIntent)
    }

    private fun isFirstStart() : Boolean {
        val preferences = getSharedPreferences("preferences", MODE_PRIVATE)!!
        return if(preferences.getBoolean("isFirstStart", true)){
            val preferences : SharedPreferences = getSharedPreferences("preferences", MODE_PRIVATE)!!
            val editor : SharedPreferences.Editor = preferences.edit()
            editor.putBoolean("isFirstStart", false)
            true
        } else false
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }
}