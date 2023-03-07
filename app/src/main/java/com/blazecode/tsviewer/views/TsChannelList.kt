/*
 *
 *  * Copyright (c) BlazeCode / Ralf Lehmann, 2023.
 *
 */

package com.blazecode.tsviewer.views

import android.graphics.Paint
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
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
    if (isSpacer(title)){
        val regex = Regex(".\$")
        val char = regex.find(title)!!.value
        val charWidth = Paint().measureText(char)

        BoxWithConstraints(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
            val n = (maxWidth / charWidth).value.toInt() / 6 * 4
            Text(text = buildString {
                for (i in 1..n) {
                    append(char)
                }
            })
        }
    } else {
        Card(modifier = Modifier.fillMaxWidth()) {
            Text(text = title)
        }
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

private fun isSpacer(name: String): Boolean {
    val pattern = Regex("^\\[\\*spacer\\d\\]")
    val result = pattern.containsMatchIn(name)
    return result
}

@Preview
@Composable
private fun Preview(){
    val demoList = mutableListOf<TsChannel>(
        TsChannel(name = "Channel 1", members = mutableListOf(TsClient(nickname = "Member 1"), TsClient(nickname = "Member 2"))),
        TsChannel(name = "[*spacer1]_"),
        )
    TsChannelList(channels = demoList)
}
