package com.blazecode.tsviewer.util.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class UserCount(
    @PrimaryKey(autoGenerate = true) val id: Int?,
    @ColumnInfo(name = "timeStamp") val timestamp : Long?,
    @ColumnInfo(name = "amount") val amount: Int?,
    @ColumnInfo(name = "names") val names: String?
) {
}