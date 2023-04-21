/*
 *
 *  * Copyright (c) BlazeCode / Ralf Lehmann, 2023.
 *
 */

package com.blazecode.tsviewer

import android.content.SharedPreferences
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import androidx.work.WorkManager
import com.blazecode.eventtool.views.DefaultPreference
import com.blazecode.eventtool.views.SwitchPreference
import com.blazecode.tsviewer.navigation.NavRoutes
import com.blazecode.tsviewer.screens.About
import com.blazecode.tsviewer.screens.Data
import com.blazecode.tsviewer.screens.Home
import com.blazecode.tsviewer.screens.Introduction
import com.blazecode.tsviewer.screens.Settings
import com.blazecode.tsviewer.ui.theme.TSViewerTheme
import com.blazecode.tsviewer.util.notification.ClientNotificationManager
import com.blazecode.tsviewer.util.wear.WearDataManager
import com.blazecode.tsviewer.viewmodels.AboutViewModel
import com.blazecode.tsviewer.viewmodels.DataViewModel
import com.blazecode.tsviewer.viewmodels.HomeViewModel
import com.blazecode.tsviewer.viewmodels.IntroductionViewModel
import com.blazecode.tsviewer.viewmodels.SettingsViewModel
import com.blazecode.tsviewer.views.BottomNavBar
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.composable
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import timber.log.Timber


class MainActivity : AppCompatActivity() {

    private lateinit var workManager: WorkManager
    private val TAG = "updater"

    private lateinit var preferences : SharedPreferences

    @OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val isFirstStart = isFirstStart()

        setContent {
            val navController = rememberAnimatedNavController()

            var isDebugMenuOpen = remember { mutableStateOf(false) }
            val startDestination = if(isFirstStart) NavRoutes.Introduction.route else NavRoutes.Home.route

            TSViewerTheme {
                Scaffold (
                    bottomBar = {
                        BottomNavBar(
                            navController = navController,
                            openDebugMenu = {
                                isDebugMenuOpen.value = true
                            }
                        )
                    },
                    content = { paddingValues ->
                        AnimatedNavHost(navController = navController, startDestination = startDestination, modifier = Modifier.padding(paddingValues).fillMaxSize()){
                            composable(NavRoutes.Home.route) { Home(HomeViewModel(application), navController) }
                            composable(NavRoutes.Data.route) { Data(DataViewModel(application), navController) }
                            composable(NavRoutes.Settings.route) { Settings(SettingsViewModel(application), navController) }
                            composable(NavRoutes.About.route) { About(AboutViewModel(application), navController) }
                            composable(NavRoutes.Introduction.route) { Introduction(IntroductionViewModel(application), navController) }
                        }
                        if(isDebugMenuOpen.value && BuildConfig.DEBUG) {
                            DebugMenu(
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

    @Composable
    private fun DebugMenu(onDismiss : () -> Unit, navController: NavController){
        val forceNoCredentials = remember { mutableStateOf(preferences.getBoolean("debug_forceNoCredentials", false)) }
        AlertDialog(
            title = { Text("Debug Menu") },
            text = {
                Column {
                    SwitchPreference(
                        title = "Force no credentials",
                        checked = forceNoCredentials.value,
                        onCheckChanged = {
                            forceNoCredentials.value = it
                            preferences.edit().putBoolean("debug_forceNoCredentials", it).apply() },
                        summary = "Force loading anim"
                    )
                    DefaultPreference(
                        title = "Start introduction",
                        summary = "Navigate to introduction screen",
                        onClick = {
                            navController.navigate(NavRoutes.Introduction.route)
                            onDismiss()
                        }
                    )
                    DefaultPreference(
                        title = "Message Wearable",
                        summary = "Send test message to wearable",
                        onClick = {
                            val wearDataManager = WearDataManager(this@MainActivity)
                            wearDataManager.sendTestMessage()
                        }
                    )
                }
            },
            confirmButton = {},
            dismissButton = { OutlinedButton(onClick = { onDismiss() }) { Text(stringResource(R.string.close)) } },
            onDismissRequest = { onDismiss() },
            modifier = Modifier.fillMaxWidth()
        )
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