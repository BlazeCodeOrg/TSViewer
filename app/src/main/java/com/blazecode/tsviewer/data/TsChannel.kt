/*
 *
 *  * Copyright (c) BlazeCode / Ralf Lehmann, 2023.
 *
 */

package com.blazecode.tsviewer.data

data class TsChannel(
    val name: String,
    val members: MutableList<TsClient> = mutableListOf()
){
    fun isEmpty() : Boolean { return members.isEmpty() }
}
