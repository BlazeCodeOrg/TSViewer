/*
 *
 *  * Copyright (c) BlazeCode / Ralf Lehmann, 2022.
 *
 */

package com.blazecode.tsviewer.wear

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.ExperimentalAnimationApi
import com.blazecode.tsviewer.wear.navigation.NavRoutes
import com.blazecode.tsviewer.wear.screens.ClientList
import com.blazecode.tsviewer.wear.screens.Home
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.composable
import com.google.accompanist.navigation.animation.rememberAnimatedNavController

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalAnimationApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            // CHECK IF COMPLICATION WAS TAPPED
            val complicationTapped = intent.extras?.getBoolean("openClientScreen")
            val startDestination = if(complicationTapped != null) NavRoutes.ClientList.route else NavRoutes.Home.route

            val navController = rememberAnimatedNavController()
            AnimatedNavHost(navController = navController, startDestination = startDestination){
                composable(NavRoutes.Home.route) { Home(navController) }
                composable(NavRoutes.ClientList.route) { ClientList(navController) }
            }
        }
    }
}