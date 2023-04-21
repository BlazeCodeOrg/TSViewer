/*
 *
 *  * Copyright (c) BlazeCode / Ralf Lehmann, 2023.
 *
 */

package com.blazecode.tsviewer.navigation

import com.blazecode.tsviewer.R

sealed class NavBarItem(val title: String, val icon: Int, val route: String) {
    object Home: NavBarItem("Home", R.drawable.ic_home, NavRoutes.Home.route)
    object Data: NavBarItem("Data", R.drawable.ic_insights, NavRoutes.Data.route)
    object Settings: NavBarItem("Settings", R.drawable.ic_settings, NavRoutes.Settings.route)
}