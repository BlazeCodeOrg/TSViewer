/*
 *
 *  * Copyright (c) BlazeCode / Ralf Lehmann, 2023.
 *
 */

package com.blazecode.tsviewer.navigation

import com.blazecode.tsviewer.R

sealed class NavBarItem(val title: String, val icon: Int, route: NavRoutes) {
    object Home: NavBarItem("Home", R.drawable.round_home_24, NavRoutes.Home)
    object Data: NavBarItem("Data", R.drawable.round_home_24, NavRoutes.Data)
    object Settings: NavBarItem("Settings", R.drawable.ic_settings, NavRoutes.Settings)
}