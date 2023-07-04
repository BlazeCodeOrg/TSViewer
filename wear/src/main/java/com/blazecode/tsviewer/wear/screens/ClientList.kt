/*
 *
 *  * Copyright (c) BlazeCode / Ralf Lehmann, 2023.
 *
 */

package com.blazecode.tsviewer.wear.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.focusable
import androidx.compose.foundation.gestures.animateScrollBy
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.input.rotary.onPreRotaryScrollEvent
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.wear.compose.foundation.lazy.ScalingLazyColumn
import androidx.wear.compose.foundation.lazy.rememberScalingLazyListState
import androidx.wear.compose.material.Chip
import androidx.wear.compose.material.ChipDefaults
import androidx.wear.compose.material.Icon
import androidx.wear.compose.material.Text
import com.blazecode.tsviewer.R
import com.blazecode.tsviewer.wear.viewmodels.ClientListViewModel
import kotlinx.coroutines.launch

@Preview
@Composable
fun ClientList(viewModel: ClientListViewModel = viewModel()){
    val uiState = viewModel.uiState.collectAsState()
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

        item { Text(text = pluralStringResource(id =R.plurals.clients_connected, count = uiState.value.clientList.size, uiState.value.clientList.size), fontSize = 16.sp) }
        item { Text(text = stringResource(R.string.time_ago, uiState.value.time), modifier = Modifier.alpha(.7f)) }
        item { Spacer(modifier = Modifier.size(16.dp)) }
        item { Text(text = uiState.value.clientListString, textAlign = TextAlign.Center) }
        item {
            Chip(
                onClick = {
                    if(!uiState.value.isLoading && !uiState.value.isDoneLoading)
                        viewModel.requestRefresh()
               },
                label = {
                    val text =
                        if(uiState.value.isLoading) stringResource(id = R.string.loading)
                        else if(uiState.value.isDoneLoading) stringResource(id = R.string.done)
                        else stringResource(id = R.string.refresh)

                    AnimatedContent (
                        targetState = text,
                        transitionSpec = {
                            fadeIn() togetherWith fadeOut()
                        }){ targetText ->
                        Text(text = targetText)
                    }
                },
                icon = { Icon(painterResource(R.drawable.ic_refresh), contentDescription = null) },
                colors = ChipDefaults.chipColors(backgroundColor = colorResource(R.color.background), iconColor = colorResource(R.color.primary)))
        }
    }
}