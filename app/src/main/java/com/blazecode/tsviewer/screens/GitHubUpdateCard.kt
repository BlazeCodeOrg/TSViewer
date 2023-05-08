/*
 *
 *  * Copyright (c) BlazeCode / Ralf Lehmann, 2023.
 *
 */

package com.blazecode.tsviewer.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.blazecode.tsviewer.R

@Composable
fun GitHubUpdateCard(title: String, description: String, onClickDownload : () -> Unit){
    val isCardExtended = remember { mutableStateOf(false) }
    val expandMoreIcon = painterResource(R.drawable.ic_expand_more)
    val expandLessIcon = painterResource(R.drawable.ic_expand_less)
    var icon = painterResource(R.drawable.ic_expand_more)
    Card (modifier = Modifier.fillMaxWidth().padding(dimensionResource(R.dimen.medium_padding))){
        Column {
            Row (verticalAlignment = Alignment.CenterVertically){
                Text(modifier = Modifier.padding(dimensionResource(R.dimen.medium_padding)).weight(2f),
                    text = title,
                    softWrap = true
                )

                Box(modifier = Modifier.fillMaxWidth().padding(dimensionResource(R.dimen.medium_padding)).weight(2f), contentAlignment = Alignment.CenterEnd){
                    Row {
                        Button(onClick = { onClickDownload() }) { Text(stringResource(R.string.download)) }
                        IconButton(onClick = {
                            isCardExtended.value = !isCardExtended.value
                            if(isCardExtended.value) icon = expandLessIcon
                            else icon = expandMoreIcon
                        }) { Icon(icon, "extend") }
                    }
                }
            }
            AnimatedVisibility(isCardExtended.value){
                Text(modifier = Modifier.padding(dimensionResource(R.dimen.medium_padding)), text = description)
            }
        }
    }
}