/*
 *
 *  * Copyright (c) BlazeCode / Ralf Lehmann, 2023.
 *
 */

package com.blazecode.tsviewer.screens

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.blazecode.tsviewer.ui.theme.TSViewerTheme
import com.blazecode.tsviewer.viewmodels.SettingsViewModel

@Composable
fun Settings(viewmodel: SettingsViewModel = viewModel()) {
    TSViewerTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            MainLayout()
        }
    }
}

@Composable
private fun MainLayout() {
    Text("Connection", modifier = Modifier.fillMaxSize())
}