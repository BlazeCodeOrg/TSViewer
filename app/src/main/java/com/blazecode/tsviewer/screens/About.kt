/*
 *
 *  * Copyright (c) BlazeCode / Ralf Lehmann, 2023.
 *
 */

package com.blazecode.tsviewer.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.blazecode.tsviewer.R
import com.blazecode.tsviewer.ui.theme.TSViewerTheme
import com.blazecode.tsviewer.viewmodels.AboutViewModel
import com.mikepenz.aboutlibraries.ui.compose.LibrariesContainer
import com.mikepenz.aboutlibraries.ui.compose.LibraryDefaults

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun About(viewModel: AboutViewModel = viewModel(), navController: NavController) {
    TSViewerTheme {
        val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())
        Scaffold(
            contentWindowInsets = WindowInsets(0.dp),
            topBar = { TopAppBar(scrollBehavior, navController) },
            modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
            content = { paddingValues ->
                Column(modifier = Modifier.padding(paddingValues)) {
                    MainLayout(viewModel)
                }
            },
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
private fun MainLayout(viewModel: AboutViewModel){
    val uiState = viewModel.uiState.collectAsState()

    val primary = MaterialTheme.colorScheme.primary
    val secondary = MaterialTheme.colorScheme.secondary

    Column() {
        Text(
            text = stringResource(R.string.app_name),
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier
                .fillMaxWidth()
                .graphicsLayer(alpha = 0.99f)
                .drawWithCache {
                    val brush = Brush.horizontalGradient(listOf(primary, secondary))
                    onDrawWithContent {
                        drawContent()
                        drawRect(brush, blendMode = BlendMode.SrcAtop)
                    }
                },
            textAlign = TextAlign.Center
        )

        FlowRow(modifier = Modifier.padding(dimensionResource(R.dimen.medium_padding)).fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
            AssistChip(
                onClick = { viewModel.copyVersion() },
                label = { Text(text = "${stringResource(R.string.version)}: ${uiState.value.versionName}") },
                leadingIcon = { Icon(painterResource(R.drawable.ic_info), "version") },
                modifier = Modifier.padding(horizontal = dimensionResource(R.dimen.small_padding))
            )
            AssistChip(
                onClick = { viewModel.openGithubIssues() },
                label = { Text(text = stringResource(R.string.report_issue)) },
                leadingIcon = { Icon(painterResource(R.drawable.ic_mail), "report") },
                modifier = Modifier.padding(horizontal = dimensionResource(R.dimen.small_padding))
            )
            AssistChip(
                onClick = { viewModel.openGithubIssues() },
                label = { Text(text = stringResource(R.string.suggest_feature)) },
                leadingIcon = { Icon(painterResource(R.drawable.ic_suggest), "suggest feature") },
                modifier = Modifier.padding(horizontal = dimensionResource(R.dimen.small_padding))
            )
            AssistChip(
                onClick = { viewModel.openSource() },
                label = { Text(text = stringResource(R.string.source)) },
                leadingIcon = { Icon(painterResource(R.drawable.ic_github), "open source") },
                modifier = Modifier.padding(horizontal = dimensionResource(R.dimen.small_padding))

            )
            AssistChip(
                onClick = { viewModel.helpTranslate() },
                label = { Text(text = stringResource(R.string.help_translate)) },
                leadingIcon = { Icon(painterResource(R.drawable.ic_translate), "help translate") },
                modifier = Modifier.padding(horizontal = dimensionResource(R.dimen.small_padding))

            )
        }

        Text(
            text = stringResource(R.string.licenses),
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(dimensionResource(R.dimen.medium_padding)))

        LibrariesContainer(
            modifier = Modifier.fillMaxSize(),
            colors = LibraryDefaults.libraryColors(
                backgroundColor = MaterialTheme.colorScheme.background,
                contentColor = MaterialTheme.colorScheme.onBackground,
                badgeBackgroundColor = MaterialTheme.colorScheme.primaryContainer,
                badgeContentColor = MaterialTheme.colorScheme.onPrimaryContainer
            )
        )
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TopAppBar(scrollBehavior: TopAppBarScrollBehavior, navController: NavController){
    LargeTopAppBar(
        title = { Text(text = stringResource(R.string.about)) },
        navigationIcon = {
            Box (modifier = Modifier.size(dimensionResource(R.dimen.icon_button_size)).clickable { navController.popBackStack() },
                contentAlignment = Alignment.Center){
                Icon(painterResource(R.drawable.ic_back), "back")
            }
        },
        scrollBehavior = scrollBehavior
    )
}