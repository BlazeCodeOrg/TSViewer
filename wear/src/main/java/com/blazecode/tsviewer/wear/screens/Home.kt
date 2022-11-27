/*
 *
 *  * Copyright (c) BlazeCode / Ralf Lehmann, 2022.
 *
 */

package com.blazecode.tsviewer.wear.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import com.blazecode.tsviewer.wear.theme.TSViewerTheme

@Composable
fun Home(navController: NavController) {
    TSViewerTheme {
        MainLayout()
    }
}

@Composable
private fun MainLayout(){
    val text = remember { mutableStateOf("") }
    val context = LocalContext.current
    Box (modifier = Modifier.fillMaxSize()){
        Column (verticalArrangement = Arrangement.Center){

        }
    }
}