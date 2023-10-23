/*
 *
 *  * Copyright (c) BlazeCode / Ralf Lehmann, 2023.
 *
 */

package com.blazecode.tsviewer.database

import androidx.room.*
import com.blazecode.tsviewer.data.TsServerInfo

@Dao
interface ServerDAO{
    @Query("SELECT * FROM tsserverinfo")
    fun getAll(): MutableList<TsServerInfo>

    @Query("SELECT * FROM tsserverinfo ORDER BY timestamp DESC LIMIT 288")      // 288 = 3 Days at 15 min interval
    fun getLast3Days(): MutableList<TsServerInfo>

    @Insert
    fun insertServerInfo(vararg serverInfo: TsServerInfo)

    @Query("SELECT timestamp FROM tsserverinfo ORDER BY timestamp DESC LIMIT 1")
    fun getTimestampOfLastUpdate(): Long
}