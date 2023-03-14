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

    @Insert
    fun insertServerInfo(vararg serverInfo: TsServerInfo)
}