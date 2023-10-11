/*
 *
 *  * Copyright (c) BlazeCode / Ralf Lehmann, 2022.
 *
 */

package com.blazecode.tsviewer.wear.navigation

sealed class NavRoutes(val route: String) {
    object Home: NavRoutes("home")
    object ClientList: NavRoutes("clientList")
    object ServiceOffScreen: NavRoutes("serviceOffScreen")
}