/*
 *
 *  * Copyright (c) BlazeCode / Ralf Lehmann, 2023.
 *
 */

package com.blazecode.tsviewer.views

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.tooling.preview.Preview
import com.blazecode.tsviewer.R
import com.blazecode.tsviewer.data.TsChannel
import com.blazecode.tsviewer.data.TsClient

@Composable
fun TsChannelList(
    channels: List<TsChannel>
    ) {
    LazyColumn {
        for(channel in channels) {
            item {
                ChannelView(title = channel.name)
            }
            if(!channel.isEmpty()){
                for(member in channel.members){
                    item {
                        MemberView(title = member.nickname)
                    }
                }
            }
        }
    }
}

@Composable
private fun ChannelView(
    title: String
){
    Card(modifier = Modifier.fillMaxWidth()) {
        Text(text = title)
    }
}

@Composable
private fun MemberView(
    title: String
){
    Card(modifier = Modifier.fillMaxWidth().padding(start = dimensionResource(R.dimen.large_padding))) {
        Text(text = title)
    }
}

@Preview
@Composable
private fun Preview(){
    val demoList = mutableListOf<TsChannel>(
        TsChannel(name = "Channel 1", members = mutableListOf(TsClient(nickname = "Member 1"), TsClient(nickname = "Member 2"))),
    )
    TsChannelList(channels = demoList)
}
