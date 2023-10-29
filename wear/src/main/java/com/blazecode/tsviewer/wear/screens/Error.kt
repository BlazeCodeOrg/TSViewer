package com.blazecode.tsviewer.wear.screens

import androidx.compose.foundation.Image
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
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.input.rotary.onPreRotaryScrollEvent
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
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
import com.blazecode.tsviewer.wear.theme.TSViewerTheme
import com.blazecode.tsviewer.wear.viewmodels.ErrorViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun Error(viewModel: ErrorViewModel = viewModel()) {
    TSViewerTheme {
        MainLayout(viewModel)
    }
}

@Composable
private fun MainLayout(viewModel: ErrorViewModel){
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

        item { Image(painter = painterResource(id = R.drawable.ic_error), contentDescription = "error") }
        item { Spacer(modifier = Modifier.size(2.dp)) }

        item { Text(text = uiState.value.error, fontSize = 16.sp) }
        item { Spacer(modifier = Modifier.size(8.dp)) }

        item {
            Chip(onClick = { scope.launch(Dispatchers.IO) { viewModel.launchApp() }},
                label = { Text(stringResource(R.string.launch_app_on_phone)) },
                icon = { Icon(painterResource(R.drawable.ic_open), contentDescription = null) },
                colors = ChipDefaults.chipColors(backgroundColor = colorResource(R.color.background), iconColor = colorResource(R.color.primary))) }
    }
}

