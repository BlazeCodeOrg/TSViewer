/*
 *
 *  * Copyright (c) BlazeCode / Ralf Lehmann, 2023.
 *
 */

package com.blazecode.tsviewer.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.blazecode.tsviewer.data.TsServerInfo
import com.blazecode.tsviewer.util.typeconverters.ClientListTypeConverter

@Database(
    version = 1,
    entities = [TsServerInfo::class],
    exportSchema = true
)
@TypeConverters(ClientListTypeConverter::class)
abstract class ServerDatabase : RoomDatabase() {
    abstract fun serverDao(): ServerDAO

    companion object {
        fun build(context: Context) = Room.databaseBuilder(context, ServerDatabase::class.java, "server-info").build()
    }
}