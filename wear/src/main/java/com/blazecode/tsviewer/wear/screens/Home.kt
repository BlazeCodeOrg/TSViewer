/*
 *
 *  * Copyright (c) BlazeCode / Ralf Lehmann, 2023.
 *
 */

package com.blazecode.tsviewer.wear.screens

import androidx.compose.foundation.focusable
import androidx.compose.foundation.gestures.animateScrollBy
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.input.rotary.onPreRotaryScrollEvent
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.wear.compose.material.*
import com.blazecode.tsviewer.BuildConfig
import com.blazecode.tsviewer.R
import com.blazecode.tsviewer.wear.navigation.NavRoutes
import com.blazecode.tsviewer.wear.theme.TSViewerTheme
import kotlinx.coroutines.launch

@Composable
fun Home(navController: NavController) {
    TSViewerTheme {
        MainLayout(navController)
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
private fun MainLayout(navController: NavController){

    val context = LocalContext.current
    val scrollState = rememberScalingLazyListState()
    val scope = rememberCoroutineScope()
    val focusRequester = remember { FocusRequester() }
    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    ScalingLazyColumn (modifier = Modifier
        .onPreRotaryScrollEvent {
            scope.launch {
                scrollState.scrollBy(it.verticalScrollPixels)
                scrollState.animateScrollBy(0f)
            }
            true
        }
        .focusRequester(focusRequester)
        .focusable()
        .fillMaxSize(),
        state = scrollState
    ){

        item {
            Row (verticalAlignment = Alignment.CenterVertically){
                Icon(painterResource(R.drawable.ic_icon), contentDescription = null)
                Spacer(modifier = Modifier.width(16.dp))
                Text(text = stringResource(R.string.app_name), fontSize = 20.sp) }
            }
        item { Spacer(modifier = Modifier.size(8.dp)) }
        item {
            Chip(onClick = { navController.navigate(NavRoutes.ClientList.route) },
                label = { Text(stringResource(R.string.open_client_screen)) },
                icon = { Icon(painterResource(R.drawable.ic_clients), contentDescription = null) },
                colors = ChipDefaults.chipColors(backgroundColor = colorResource(R.color.background), iconColor = colorResource(R.color.primary))) }
        /*
        item {
            Chip(onClick = { scope.launch(Dispatchers.IO) { WearDataManager(context).launchAppOnPhone(System.currentTimeMillis()) }},
                label = { Text(stringResource(R.string.launch_app_on_phone)) },
                icon = { Icon(painterResource(R.drawable.ic_open), contentDescription = null) },
                colors = ChipDefaults.chipColors(backgroundColor = colorResource(R.color.background), iconColor = colorResource(R.color.primary))) }
         */

        item { Spacer(modifier = Modifier.size(8.dp)) }
        item { Text(text = "Version: ${BuildConfig.VERSION_NAME}", textAlign = TextAlign.Center, modifier = Modifier.alpha(.7f)) }
    }
}
