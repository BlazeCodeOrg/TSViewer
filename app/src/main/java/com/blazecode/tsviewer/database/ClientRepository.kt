/*
 *
 *  * Copyright (c) BlazeCode / Ralf Lehmann, 2023.
 *
 */

package com.blazecode.tsviewer.database

import android.content.Context
import com.blazecode.tsviewer.data.TsClient

class ClientRepository(context: Context) {

    val database = ClientDatabase.build(context)
    val clientDao = database.clientDao()

    fun getAllClients(): MutableList<TsClient> {
        return clientDao.getAll()
    }

    fun getClientById(id: Int): TsClient? {
        return clientDao.getClientById(id)
    }

    fun insertClient(client: TsClient) {
        clientDao.insertClientData(client)
    }

    fun deleteClient(client: TsClient) {
        clientDao.delete(client)
    }
}