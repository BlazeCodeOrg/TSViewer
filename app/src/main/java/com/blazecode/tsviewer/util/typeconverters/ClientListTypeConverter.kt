/*
 *
 *  * Copyright (c) BlazeCode / Ralf Lehmann, 2023.
 *
 */

package com.blazecode.tsviewer.util.typeconverters

import androidx.room.TypeConverter
import com.blazecode.tsviewer.data.TsClient
import java.util.*

class ClientListTypeConverter {

    @TypeConverter
    fun fromList(list: MutableList<TsClient>): String {
        var string = ""
        for (client in list) {
            string += "$client;"
        }
        return string
    }

    @TypeConverter
    fun toList(string: String): MutableList<TsClient> {
        val list = mutableListOf<TsClient>()
        val split = string.split(";")
        for (client in split) {
            val subsplit = client.split(",")
            list.add(TsClient(
                id = subsplit[0].toInt(),
                nickname = subsplit[1],
                lastSeen = Date(subsplit[4].toLong()),
                activeConnectionTime = subsplit[5].toLong(),
            ))
        }
        return list
    }
}