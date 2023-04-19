/*
 *
 *  * Copyright (c) BlazeCode / Ralf Lehmann, 2023.
 *
 */

package com.blazecode.tsviewer.navigation

sealed class NavRoutes(val route: String) {
    object Home: NavRoutes("home")
    object Data: NavRoutes("data")
    object Settings: NavRoutes("settings")
    object About: NavRoutes("about")
    object Introduction: NavRoutes("introduction")
}