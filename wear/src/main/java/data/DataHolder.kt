/*
 *
 *  * Copyright (c) BlazeCode / Ralf Lehmann, 2023.
 *
 */

package data

import data.TsClient

object DataHolder {
    var list: MutableList<TsClient> = mutableListOf()
    var time: Long = 0
}