/*
 *
 *  * Copyright (c) BlazeCode / Ralf Lehmann, 2023.
 *
 */

package com.blazecode.tsviewer.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.blazecode.tsviewer.uistate.IntroductionUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class IntroductionViewModel(val app: Application) : AndroidViewModel(app) {

    // UI STATE
    private val _uiState = MutableStateFlow(IntroductionUiState())
    val uiState: StateFlow<IntroductionUiState> = _uiState.asStateFlow()

}
