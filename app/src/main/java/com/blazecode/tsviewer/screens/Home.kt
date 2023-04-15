/*
 *
 *  * Copyright (c) BlazeCode / Ralf Lehmann, 2023.
 *
 */

package com.blazecode.tsviewer.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.blazecode.eventtool.views.SwitchBar
import com.blazecode.tsviewer.R
import com.blazecode.tsviewer.navigation.NavRoutes
import com.blazecode.tsviewer.ui.theme.TSViewerTheme
import com.blazecode.tsviewer.ui.theme.Typography
import com.blazecode.tsviewer.viewmodels.HomeViewModel
import com.blazecode.tsviewer.views.TsChannelList

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Home(viewModel: HomeViewModel = viewModel(), navController: NavController) {
    TSViewerTheme {
        val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())
        Scaffold (
            topBar = { TopAppBar(scrollBehavior) },
            modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
            content = { paddingValues ->
                Box(modifier = Modifier.padding(paddingValues).fillMaxSize()){
                    MainLayout(viewModel, navController)
                }
            }
        )
    }
}

@Composable
private fun MainLayout(viewModel: HomeViewModel, navController: NavController) {
    val uiState = viewModel.uiState.collectAsState()

    Column {
        SwitchBar(
            title = stringResource(R.string.run_service),
            checked = uiState.value.serviceRunning,
            onCheckChanged = {
                if(uiState.value.areCredentialsSet){
                    viewModel.setRunService(it)
                } else {
                    viewModel.setRunService(false)
                }
            }
        )
        Box(modifier = Modifier.padding(dimensionResource(R.dimen.medium_padding), dimensionResource(R.dimen.medium_padding), dimensionResource(R.dimen.medium_padding))){
            if(!uiState.value.areCredentialsSet || uiState.value.channels.isEmpty()){
                println("No credentials set or loading")
                Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
                    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.lottie_no_connection))
                    val progress by animateLottieCompositionAsState(composition = composition, iterations = LottieConstants.IterateForever)
                    LottieAnimation(
                        composition = composition,
                        progress = { progress },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp),
                    )
                    val annotatedText = buildAnnotatedString {
                        append(stringResource(R.string.no_credentials_found))
                        append(" ")
                        pushStringAnnotation(
                            tag = "url", annotation = NavRoutes.Settings.route
                        )
                        withStyle(
                                style = SpanStyle(
                                    color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold
                                )
                        ) {
                            append(stringResource(R.string.settings))
                        }
                        pop()
                    }
                    ClickableText( annotatedText, onClick = { offset ->
                            annotatedText.getStringAnnotations(
                                tag = "url",
                                start = offset,
                                end = offset
                            ).firstOrNull()?.let { annotation ->
                                navController.navigate(annotation.item)
                            }
                        }
                    )
                }
            } else {
                println("Done Loading")
                TsChannelList(
                    channels = uiState.value.channels,
                    onClickChannel = { channel -> println(channel.toString()) },
                    onClickMember = { member -> println(member.toString()) }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TopAppBar(scrollBehavior: TopAppBarScrollBehavior){
    LargeTopAppBar(
        title = { Text(text = stringResource(R.string.app_name), style = Typography.titleLarge) },
        scrollBehavior = scrollBehavior
    )
}