/*
 *
 *  * Copyright (c) BlazeCode / Ralf Lehmann, 2023.
 *
 */

package com.blazecode.tsviewer.wear.data

import androidx.lifecycle.MutableLiveData
import com.blazecode.tsviewer.wear.enum.ErrorCode

object DataHolder {
    val list: MutableLiveData<MutableList<TsClient>> by lazy {
        MutableLiveData<MutableList<TsClient>>()
    }
    val time: MutableLiveData<Long> by lazy {
        MutableLiveData<Long>()
    }
    val serviceStatus: MutableLiveData<Boolean> by lazy {
        MutableLiveData<Boolean>()
    }
    val errorCode: MutableLiveData<ErrorCode> by lazy {
        MutableLiveData<ErrorCode>()
    }
}