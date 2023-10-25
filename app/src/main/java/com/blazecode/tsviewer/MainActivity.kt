/*
 *
 *  * Copyright (c) BlazeCode / Ralf Lehmann, 2023.
 *
 */

package com.blazecode.tsviewer

import android.content.SharedPreferences
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.work.WorkManager
import com.blazecode.tsviewer.navigation.NavRoutes
import com.blazecode.tsviewer.screens.About
import com.blazecode.tsviewer.screens.Data
import com.blazecode.tsviewer.screens.Introduction
import com.blazecode.tsviewer.ui.theme.TSViewerTheme
import com.blazecode.tsviewer.util.notification.ClientNotificationManager
import com.blazecode.tsviewer.viewmodels.AboutViewModel
import com.blazecode.tsviewer.viewmodels.DataViewModel
import com.blazecode.tsviewer.viewmodels.IntroductionViewModel
import com.blazecode.tsviewer.views.BottomNavBar
import screens.Home
import screens.Settings
import timber.log.Timber
import viewmodels.HomeViewModel
import viewmodels.SettingsViewModel
import views.DebugMenu


class MainActivity : AppCompatActivity() {

    private lateinit var workManager: WorkManager
    private val TAG = "updater"

    private lateinit var preferences : SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        val isFirstStart = isFirstStart()

        setContent {
            val navController = rememberNavController()

            val isDebugMenuOpen = remember { mutableStateOf(false) }
            val startDestination = if(isFirstStart) NavRoutes.Introduction.route else NavRoutes.Home.route

            TSViewerTheme {
                Scaffold (
                    contentWindowInsets = WindowInsets(0.dp),
                    bottomBar = {
                        BottomNavBar(
                            navController = navController,
                            openDebugMenu = {
                                isDebugMenuOpen.value = true
                            }
                        )
                    },
                    content = { paddingValues ->
                        NavHost(navController = navController, startDestination = startDestination, modifier = Modifier.padding(paddingValues).fillMaxSize()){
                            composable(NavRoutes.Home.route) { Home(HomeViewModel(application), navController) }
                            composable(NavRoutes.Data.route) { Data(DataViewModel(application), navController) }
                            composable(NavRoutes.Settings.route) { Settings(SettingsViewModel(application), navController) }
                            composable(NavRoutes.About.route) { About(AboutViewModel(application), navController) }
                            composable(NavRoutes.Introduction.route) { Introduction(IntroductionViewModel(application), navController) }
                        }
                        if(isDebugMenuOpen.value && BuildConfig.DEBUG) {
                            DebugMenu(
                                context = this@MainActivity,
                                preferences = preferences,
                                onDismiss = {
                                    isDebugMenuOpen.value = false
                                },
                                navController = navController
                            )
                        }
                    }
                )
            }
        }

        // START LOGGING
        Timber.plant(Timber.DebugTree())

        // PREFERENCES
        preferences = getSharedPreferences("preferences", MODE_PRIVATE)!!

        // INITIALIZE WORK MANAGER
        workManager = this.let { WorkManager.getInstance(it) }

        //CREATE NOTIFICATION CHANNEL IF FIRST START
        if (isFirstStart){
            val clientNotificationManager = ClientNotificationManager(this)
            clientNotificationManager.createChannel()
        }
    }

    private fun isFirstStart() : Boolean {
        val preferences = getSharedPreferences("preferences", MODE_PRIVATE)!!
        return if(preferences.getBoolean("isFirstStart", true)){
            val prefs : SharedPreferences = getSharedPreferences("preferences", MODE_PRIVATE)!!
            val editor : SharedPreferences.Editor = prefs.edit()
            editor.putBoolean("isFirstStart", false)
            editor.commit()
            true
        } else false
    }
}