package com.blazecode.tsviewer.util.database

import androidx.room.*

@Dao
interface UserCountDAO {

    @Transaction
    open suspend fun userCountManager(userCount: UserCount){
        insertUserCount(userCount)
        delete(userCount)
        getAll()
    }

    @Query("SELECT * FROM usercount")
    fun getAll(): MutableList<UserCount>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertUserCount(vararg userCount: UserCount)

    @Delete
    fun delete(userCount: UserCount)
}