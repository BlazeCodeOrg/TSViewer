/*
 *
 *  * Copyright (c) BlazeCode / Ralf Lehmann, 2022.
 *
 */

package com.blazecode.tsviewer.wear.complication

import android.content.ComponentName
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ComplicationArguments(
    val component: ComponentName,
    val complication: Complication,
    val complicationId: Int
) : Parcelable