package com.blazecode.tsviewer.util.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    version = 1,
    entities = [UserCount::class],
    exportSchema = true
)
abstract class UserCountDatabase : RoomDatabase() {
    abstract fun userCountDao(): UserCountDAO

    companion object {
        fun build(context: Context) = Room.databaseBuilder(context, UserCountDatabase::class.java, "databaseUserCount").build()
    }
}