/*
 *
 *  * Copyright (c) BlazeCode / Ralf Lehmann, 2023.
 *
 */

package com.blazecode.tsviewer.screens

import androidx.compose.animation.*
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.blazecode.eventtool.views.EditTextPreference
import com.blazecode.eventtool.views.PreferenceGroup
import com.blazecode.eventtool.views.SwitchPreference
import com.blazecode.tsviewer.R
import com.blazecode.tsviewer.ui.theme.TSViewerTheme
import com.blazecode.tsviewer.viewmodels.SettingsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Settings(viewModel: SettingsViewModel = viewModel(), navController: NavController) {

    viewModel.loadSettings()

    TSViewerTheme {
        val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())
        Scaffold (
            topBar = { TopAppBar(scrollBehavior, navController) },
            modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
            content = { paddingValues ->
                Column(modifier = Modifier.padding(paddingValues).fillMaxSize().verticalScroll(rememberScrollState())){
                    MainLayout(viewModel)
                }
            }
        )
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
private fun MainLayout(viewModel: SettingsViewModel) {
    val uiState = viewModel.uiState.collectAsState()

    Column {
        PreferenceGroup(title = stringResource(R.string.connection)) {
            EditTextPreference(
                title = stringResource(R.string.ip_address),
                text = uiState.value.ip,
                icon = painterResource(R.drawable.ic_ip),
                singleLine = true,
                onTextChange = { viewModel.setIp(it) }
            )
            EditTextPreference(
                title = stringResource(R.string.query_username),
                text = uiState.value.username,
                icon = painterResource(R.drawable.ic_user),
                singleLine = true,
                onTextChange = { viewModel.setUsername(it) }
            )
            EditTextPreference(
                title = stringResource(R.string.query_password),
                text = uiState.value.password,
                icon = painterResource(R.drawable.ic_password),
                singleLine = true,
                isPassword = true,
                onTextChange = { viewModel.setPassword(it) }
            )
            EditTextPreference(
                title = stringResource(R.string.query_port),
                text = uiState.value.queryPort.toString(),
                icon = painterResource(R.drawable.ic_port),
                singleLine = true,
                isNumber = true,
                onTextChange = { viewModel.setQueryPort(it.toInt()) }
            )
            SwitchPreference(
                title = stringResource(R.string.include_query_clients),
                icon = painterResource(R.drawable.ic_query_client),
                checked = uiState.value.includeQueryClients,
                onCheckChanged = { viewModel.setIncludeQueryClients(it) }
            )
            SwitchPreference(
                title = stringResource(R.string.only_wifi),
                icon = painterResource(R.drawable.ic_wifi),
                checked = uiState.value.executeOnlyOnWifi,
                onCheckChanged = { viewModel.setExecuteOnlyOnWifi(it) }
            )
            Row (modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                OutlinedButton(
                    onClick = { if(uiState.value.connectionSuccessful == null) viewModel.testConnection() },
                    modifier = Modifier.padding(dimensionResource(R.dimen.small_padding)),
                    content = {
                        val text = if(uiState.value.connectionSuccessful == null) stringResource(R.string.test_connection)
                                        else if (uiState.value.connectionSuccessful!!) stringResource(R.string.connection_successful)
                                        else stringResource(R.string.connection_failed)

                        AnimatedContent (
                            targetState = text,
                            transitionSpec = { fadeIn() with fadeOut()
                            }){ targetText ->
                            Text(text = targetText)
                        }
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TopAppBar(scrollBehavior: TopAppBarScrollBehavior, navController: NavController){
    LargeTopAppBar(
        title = { Text(text = stringResource(R.string.settings)) },
        navigationIcon = {
            Box (modifier = Modifier.size(dimensionResource(R.dimen.icon_button_size)).clickable { navController.popBackStack() },
                contentAlignment = Alignment.Center){
                Icon(painterResource(R.drawable.ic_back), "back")
            }
        },
        scrollBehavior = scrollBehavior
    )
}