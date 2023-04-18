/*
 *
 *  * Copyright (c) BlazeCode / Ralf Lehmann, 2023.
 *
 */

package com.blazecode.tsviewer.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.blazecode.scrapguidev2.util.LinkUtil
import com.blazecode.tsviewer.BuildConfig
import com.blazecode.tsviewer.R
import com.blazecode.tsviewer.uistate.AboutUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class AboutViewModel(val app: Application): AndroidViewModel(app) {

    // UI STATE
    private val _uiState = MutableStateFlow(AboutUiState())
    val uiState: StateFlow<AboutUiState> = _uiState.asStateFlow()

    init {
        _uiState.value = _uiState.value.copy(
            versionName = BuildConfig.VERSION_NAME,
            versionCode = BuildConfig.VERSION_CODE
        )
    }

    fun openGithubIssues(){
        val issuesUrl = app.resources.getString(R.string.github_issues_url)
        LinkUtil.Builder(
            context = app,
            link = issuesUrl,
        ).open()
    }

    fun openSource(){
        val sourceUrl = app.resources.getString(R.string.github_source_url)
        LinkUtil.Builder(
            context = app,
            link = sourceUrl,
        ).open()
    }
}
