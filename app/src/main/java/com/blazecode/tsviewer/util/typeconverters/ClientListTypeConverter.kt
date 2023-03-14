/*
 *
 *  * Copyright (c) BlazeCode / Ralf Lehmann, 2023.
 *
 */

package com.blazecode.tsviewer.util.typeconverters

import androidx.room.TypeConverter
import com.blazecode.tsviewer.data.TsClient
import com.google.gson.GsonBuilder
import java.util.*

class ClientListTypeConverter {

    @TypeConverter
    fun fromList(list: MutableList<TsClient>): String {
        val jsonParser = GsonBuilder().create()
        return jsonParser.toJson(list)
    }

    @TypeConverter
    fun toList(string: String): MutableList<TsClient> {
        val jsonParser = GsonBuilder().create()
        val array = jsonParser.fromJson(string, Array<TsClient>::class.java)

        val list = mutableListOf<TsClient>()
        for (item in array){
            list.add(item)
        }
        return list
    }
}