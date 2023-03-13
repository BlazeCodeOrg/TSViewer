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
import com.blazecode.tsviewer.data.TsClient
import com.blazecode.tsviewer.util.typeconverters.DateTypeConverter

@Database(
    version = 1,
    entities = [TsClient::class],
    exportSchema = true
)
@TypeConverters(DateTypeConverter::class)
abstract class ClientDatabase : RoomDatabase() {
    abstract fun clientDao(): ClientDAO

    companion object {
        fun build(context: Context) = Room.databaseBuilder(context, ClientDatabase::class.java, "clients").build()
    }
}