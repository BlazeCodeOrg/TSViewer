/*
 *
 *  * Copyright (c) BlazeCode / Ralf Lehmann, 2023.
 *
 */

package com.blazecode.tsviewer.views

import android.graphics.Paint
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.blazecode.tsviewer.R
import com.blazecode.tsviewer.data.TsChannel
import com.blazecode.tsviewer.data.TsClient

@Composable
fun TsChannelList(
    channels: List<TsChannel>,
    onClickChannel: (TsChannel) -> Unit = {},
    onClickMember: (TsClient) -> Unit = {}
    ) {
    LazyColumn {
        for(channel in channels) {
            item {
                ChannelView(
                    title = channel.name,
                    onClick = { onClickChannel(channel) }
                )
            }
            if(!channel.isEmpty()){
                for(member in channel.members){
                    item {
                        MemberView(
                            member = member,
                            onClick = { onClickMember(member) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ChannelView(
    title: String,
    onClick: () -> Unit = {}
){
    if (isSpacer(title)){
        val regex = Regex(".\$")
        val char = regex.find(title)!!.value
        val charWidth = Paint().measureText(char)

        BoxWithConstraints(modifier = Modifier.fillMaxWidth().clickable { onClick() }, contentAlignment = Alignment.Center) {
            val n = (maxWidth / charWidth).value.toInt() / 6 * 4
            Text(text = buildString {
                for (i in 1..n) {
                    append(char)
                }
            })
        }
    } else if (isCSpacer(title)){
        val regex = Regex("\\w{1,}\$")
        val regexTitle = regex.find(title)!!.value
        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
            Card(modifier = Modifier.clickable { onClick() }) {
                Text(text = regexTitle, modifier = Modifier.padding(4.dp, 0.dp, 4.dp, 0.dp))
            }
        }
    } else {
        Card(modifier = Modifier.fillMaxWidth().clickable { onClick() }) {
            Box(modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.surfaceVariant,
                            MaterialTheme.colorScheme.background)))) {
                Text(text = title, modifier = Modifier.padding(4.dp))
            }
        }
    }
}

@Composable
private fun MemberView(
    member: TsClient,
    onClick: () -> Unit = {}
){
    Card(modifier = Modifier.padding(start = dimensionResource(R.dimen.large_padding)).clickable { onClick() }) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            if(member.isInputMuted && !member.isOutputMuted)
                Icon(painter = painterResource(id = R.drawable.ic_mic_muted),
                    modifier = Modifier.size(20.dp).padding(start = 4.dp),
                    contentDescription = null)
            if(member.isOutputMuted)
                Icon(painter = painterResource(id = R.drawable.ic_speaker_muted),
                    modifier = Modifier.size(20.dp).padding(start = 4.dp),
                    contentDescription = null)
            Text(text = member.nickname, modifier = Modifier.padding(4.dp))
        }
    }
}

private fun isSpacer(name: String): Boolean {
    val pattern = Regex("^\\[\\*spacer\\d\\]")
    return pattern.containsMatchIn(name)
}

private fun isCSpacer(name: String): Boolean {
    val pattern = Regex("^\\[cspacer\\](\\w{1,})")
    return pattern.containsMatchIn(name)
}

@Preview
@Composable
private fun Preview(){
    val demoList = mutableListOf<TsChannel>(
        TsChannel(name = "Channel 1", members = mutableListOf(TsClient(id = 0, nickname = "Member 1"), TsClient(id = 0, nickname = "Member 2"))),
        TsChannel(name = "Channel 1", members = mutableListOf(TsClient(id = 0, nickname = "Member 1", isInputMuted = true))),
        TsChannel(name = "[*spacer1]_"),
        )
    TsChannelList(channels = demoList)
}
