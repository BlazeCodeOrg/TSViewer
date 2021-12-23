package com.blazecode.tsviewer

import android.content.Context
import android.content.SharedPreferences
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
                else -> false
            }
        }

        //CREATE NOTIFICATION CHANNEL IF FIRST START
        if (isFirstStart()){
            val notificationManager = NotificationManager(this)
            notificationManager.createChannel()
        }
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