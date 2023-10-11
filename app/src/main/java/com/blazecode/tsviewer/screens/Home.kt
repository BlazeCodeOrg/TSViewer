/*
 *
 *  * Copyright (c) BlazeCode / Ralf Lehmann, 2023.
 *
 */

package screens

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
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
import com.blazecode.tsviewer.BuildConfig
import com.blazecode.tsviewer.R
import com.blazecode.tsviewer.data.TsChannel
import com.blazecode.tsviewer.navigation.NavRoutes
import com.blazecode.tsviewer.ui.theme.TSViewerTheme
import com.blazecode.tsviewer.util.updater.GitHubUpdater
import com.blazecode.tsviewer.views.TsChannelList
import viewmodels.HomeViewModel

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
    val context = LocalContext.current

    Column {
        if(BuildConfig.DEBUG && uiState.value.debug_updateAvailable || !BuildConfig.DEBUG){
            GitHubUpdater(context)
        }
        SwitchBar(
            title = if(uiState.value.serviceRunning) stringResource(R.string.service_running) else stringResource(R.string.service_stopped),
            checked = uiState.value.serviceRunning,
            summary = if(uiState.value.serviceRunning) stringResource(R.string.last_update, uiState.value.lastUpdate) else "",
            onCheckChanged = {
                if(uiState.value.areCredentialsSet){
                    viewModel.setRunService(it)
                } else {
                    viewModel.setRunService(false)
                }
            }
        )
        Box(modifier = Modifier.padding(dimensionResource(R.dimen.medium_padding), dimensionResource(R.dimen.medium_padding), dimensionResource(R.dimen.medium_padding))){
            Column {
                val currentView = remember { mutableStateOf("loading") }
                if(uiState.value.areCredentialsSet && uiState.value.channels.isNotEmpty()){
                    currentView.value = "channels"
                } else if(uiState.value.areCredentialsSet && uiState.value.channels.isEmpty()){
                    currentView.value = "loading"
                } else {
                    currentView.value = "no_credentials"
                }

                Crossfade(targetState = currentView.value, modifier = Modifier.fillMaxSize()) { view ->
                    when(view){
                        "loading" -> LoadingView()
                        "channels" -> ChannelView(channels = uiState.value.channels)
                        "no_credentials" -> NoCredentialsView(navController = navController)
                    }
                }
            }
        }
    }
}

@Composable
private fun LoadingView(){
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center){
        CircularProgressIndicator()
    }
}

@Composable
private fun ChannelView(channels : List<TsChannel>){
    TsChannelList(
        channels = channels,
        onClickChannel = { channel -> println(channel.toString()) },
        onClickMember = { member -> println(member.toString()) }
    )
}

@Composable
private fun NoCredentialsView(navController: NavController){
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
            withStyle(
                style = SpanStyle(
                    color = MaterialTheme.colorScheme.onBackground
                )
            ){
                append(stringResource(R.string.no_credentials_found))
            }
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
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TopAppBar(scrollBehavior: TopAppBarScrollBehavior){
    LargeTopAppBar(
        title = { Text(text = stringResource(R.string.app_name)) },
        scrollBehavior = scrollBehavior
    )
}