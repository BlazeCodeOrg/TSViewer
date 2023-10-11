/*
 *
 *  * Copyright (c) BlazeCode / Ralf Lehmann, 2023.
 *
 */

package com.blazecode.tsviewer.wear

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.blazecode.tsviewer.wear.data.DataHolder
import com.blazecode.tsviewer.wear.navigation.NavRoutes
import com.blazecode.tsviewer.wear.screens.ClientList
import com.blazecode.tsviewer.wear.screens.Home
import com.blazecode.tsviewer.wear.screens.ServiceOff
import com.blazecode.tsviewer.wear.viewmodels.ClientListViewModel
import com.blazecode.tsviewer.wear.viewmodels.HomeViewModel
import com.blazecode.tsviewer.wear.viewmodels.ServiceOffViewModel

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val dataHolder = DataHolder

        setContent {
            // CHECK IF COMPLICATION WAS TAPPED
            var startDestination: String = NavRoutes.Home.route

            if(intent.extras?.getBoolean("openComplication") != null){
                if(dataHolder.serviceStatus.value == true)
                    startDestination = NavRoutes.ClientList.route
                else
                    startDestination = NavRoutes.ServiceOffScreen.route
            }

            val navController = rememberNavController()
            NavHost(navController = navController, startDestination = startDestination) {
                composable(NavRoutes.Home.route) { Home(HomeViewModel(application), navController) }
                composable(NavRoutes.ClientList.route) { ClientList(ClientListViewModel(application)) }
                composable(NavRoutes.ServiceOffScreen.route) { ServiceOff(ServiceOffViewModel(application)) }
            }
        }
    }
}