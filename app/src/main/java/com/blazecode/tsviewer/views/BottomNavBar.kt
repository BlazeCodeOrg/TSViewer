/*
 *
 *  * Copyright (c) BlazeCode / Ralf Lehmann, 2023.
 *
 */

package com.blazecode.tsviewer.views

import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.blazecode.tsviewer.navigation.NavBarItem


val items = listOf(
    NavBarItem.Home,
    NavBarItem.Data,
    NavBarItem.Settings
)

@Composable
fun BottomNavBar(navController: NavController, openDebugMenu : () -> Unit){
    NavigationBar {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination
        items.forEach { screen ->
            NavigationBarItem(
                icon = { Icon(painterResource(screen.icon), screen.title) },
                label = { Text(screen.title) },
                selected = currentRoute?.route == screen.route,
                onClick = {
                    if (currentRoute?.route != screen.route) {
                        navController.navigate(screen.route)
                    } else {
                        openDebugMenu()
                    }
                }
            )
        }
    }
}
