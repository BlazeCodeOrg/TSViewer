/*
 *
 *  * Copyright (c) BlazeCode / Ralf Lehmann, 2022.
 *
 */

package com.blazecode.tsviewer.wear.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.wear.compose.material.Text
import com.blazecode.tsviewer.wear.complication.ComplicationDataHolder

@Composable
fun ClientList(navController: NavController){

    Box(modifier = Modifier.fillMaxSize()){
        Text(modifier = Modifier.align(Alignment.Center), text = ComplicationDataHolder.list.joinToString())
    }
}