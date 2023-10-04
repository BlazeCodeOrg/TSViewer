/*
 *
 *  * Copyright (c) BlazeCode / Ralf Lehmann, 2023.
 *
 */

package com.blazecode.tsviewer.wear.data

import androidx.lifecycle.MutableLiveData

object DataHolder {
    val list: MutableLiveData<MutableList<TsClient>> by lazy {
        MutableLiveData<MutableList<TsClient>>()
    }
    val time: MutableLiveData<Long> by lazy {
        MutableLiveData<Long>()
    }
}