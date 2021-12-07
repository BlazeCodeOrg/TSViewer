package com.blazecode.tsviewer

import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import com.blazecode.tsviewer.databinding.ActivityMainBinding
import com.blazecode.tsviewer.util.NotificationManager


class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


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

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_force_stop -> TODO("add force stop service")

            R.id.action_licenses -> TODO("add open source licenses")

            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }
}