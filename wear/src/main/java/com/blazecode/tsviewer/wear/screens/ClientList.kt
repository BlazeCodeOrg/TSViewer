/*
 *
 *  * Copyright (c) BlazeCode / Ralf Lehmann, 2022.
 *
 */

package com.blazecode.tsviewer.wear.screens

import androidx.compose.foundation.focusable
import androidx.compose.foundation.gestures.animateScrollBy
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.input.rotary.onPreRotaryScrollEvent
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.wear.compose.material.ScalingLazyColumn
import androidx.wear.compose.material.Text
import androidx.wear.compose.material.rememberScalingLazyListState
import com.blazecode.tsviewer.R
import com.blazecode.tsviewer.wear.complication.ComplicationDataHolder
import kotlinx.coroutines.launch

@OptIn(ExperimentalComposeUiApi::class)
@Preview
@Composable
fun ClientList(){

    val title = if(ComplicationDataHolder.list.size == 1)
        stringResource(R.string.client_connected, 1)
    else stringResource(R.string.clients_connected, ComplicationDataHolder.list.size)

    var timeAgo: String = ((System.currentTimeMillis() - ComplicationDataHolder.time) / 1000 / 60).toString()
    if(ComplicationDataHolder.time == 0L) timeAgo = "-"

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

        item { Text(text = title, fontSize = 16.sp) }
        item { Text(text = stringResource(R.string.time_ago, timeAgo), modifier = Modifier.alpha(.7f)) }
        item { Spacer(modifier = Modifier.size(16.dp)) }
        item { Text(text = ComplicationDataHolder.list.joinToString(), textAlign = TextAlign.Center) }
    }
}