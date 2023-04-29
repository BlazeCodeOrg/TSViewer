/*
 *
 *  * Copyright (c) BlazeCode / Ralf Lehmann, 2023.
 *
 */

package com.blazecode.tsviewer.views

import android.content.Context
import android.content.SharedPreferences
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import com.blazecode.eventtool.views.DefaultPreference
import com.blazecode.eventtool.views.PreferenceGroup
import com.blazecode.eventtool.views.SwitchPreference
import com.blazecode.tsviewer.R
import com.blazecode.tsviewer.navigation.NavRoutes
import com.blazecode.tsviewer.util.DemoModeValues
import com.blazecode.tsviewer.util.notification.ClientNotificationManager
import com.blazecode.tsviewer.util.tile.TileManager
import com.blazecode.tsviewer.util.wear.WearDataManager

@Composable
fun DebugMenu(context: Context, preferences: SharedPreferences, onDismiss : () -> Unit, navController: NavController) {
    val forceNoCredentials = remember { mutableStateOf(preferences.getBoolean("debug_forceNoCredentials", false)) }
    val demoMode = remember { mutableStateOf(preferences.getBoolean("debug_demoMode", false)) }
    AlertDialog(
        title = { Text("Debug Menu") },
        text = {
            Column {
                PreferenceGroup(title = "General"){
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
                        title = "Crash app",
                        summary = "trigger a crash",
                        onClick = {
                            throw Exception("Crash triggered by user")
                        }
                    )
                }
                PreferenceGroup(title = "Wearable"){
                    DefaultPreference(
                        title = "Message Wearable",
                        summary = "Send test message to wearable",
                        onClick = {
                            val wearDataManager = WearDataManager(context)
                            wearDataManager.sendTestMessage()
                        }
                    )
                }
                PreferenceGroup(title = "Demo Mode") {
                    SwitchPreference(
                        title = "Demo mode",
                        checked = demoMode.value,
                        onCheckChanged = {
                            demoMode.value = it
                            preferences.edit().putBoolean("debug_demoMode", it).apply() },
                        summary = "Show demo data"
                    )
                    DefaultPreference(
                        title = "Post notification",
                        summary = "Will contain demo values",
                        onClick = {
                            val clientNotificationManager = ClientNotificationManager(context)
                            clientNotificationManager.post(DemoModeValues.clientList())
                            onDismiss()
                        }
                    )
                    DefaultPreference(
                        title = "Update complication",
                        summary = "Will contain demo values",
                        onClick = {
                            val wearDataManager = WearDataManager(context)
                            wearDataManager.sendClientList(DemoModeValues.clientList())
                            onDismiss()
                        }
                    )
                    DefaultPreference(
                        title = "Update QS tile",
                        summary = "Will contain demo values",
                        onClick = {
                            val tileManager = TileManager(context)
                            tileManager.post(DemoModeValues.clientList())
                            onDismiss()
                        }
                    )
                }
            }
        },
        confirmButton = {},
        dismissButton = { OutlinedButton(onClick = { onDismiss() }) { Text(stringResource(R.string.close)) } },
        onDismissRequest = { onDismiss() },
        modifier = Modifier.fillMaxWidth()
    )
}