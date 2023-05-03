/*
 *
 *  * Copyright (c) BlazeCode / Ralf Lehmann, 2023.
 *
 */

package com.blazecode.eventtool.views

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.blazecode.tsviewer.R

@Composable
fun SwitchBar(
    title: String,
    summary: String = "",
    checked: Boolean,
    onCheckChanged: (Boolean) -> Unit){
    Card (modifier = Modifier.padding(dimensionResource(R.dimen.medium_padding), dimensionResource(R.dimen.small_padding), dimensionResource(R.dimen.medium_padding), 0.dp),
    colors = CardDefaults.cardColors(containerColor = getContainerColor(checked)),){
        Row (modifier = Modifier.fillMaxWidth().padding(dimensionResource(R.dimen.medium_padding)), verticalAlignment = Alignment.CenterVertically){
            Column (modifier = Modifier.weight(5f, true)){
                Text(title, color = getTextColor(checked), fontSize = 18.sp)
                if(summary.isNotEmpty()) Text(summary, color = getTextColor(checked), fontSize = 16.sp, modifier = Modifier.alpha(.7f))
            }
            Box (modifier = Modifier.fillMaxWidth().padding(0.dp, 0.dp, dimensionResource(R.dimen.medium_padding), 0.dp).weight(1.5f), contentAlignment = Alignment.CenterEnd){
                Switch(
                    checked = checked,
                    onCheckedChange = {
                        onCheckChanged(it)
                    },
                    colors = SwitchDefaults.colors(
                        checkedTrackColor = MaterialTheme.colorScheme.secondaryContainer,
                        checkedThumbColor = MaterialTheme.colorScheme.primary,
                    )
                )
            }
        }
    }
}

@Composable
private fun getContainerColor(checked: Boolean): Color {
    return if (checked) MaterialTheme.colorScheme.surfaceTint else MaterialTheme.colorScheme.surfaceVariant
}

@Composable
private fun getTextColor(checked: Boolean): Color {
    return if (checked) MaterialTheme.colorScheme.inverseOnSurface else MaterialTheme.colorScheme.onSurfaceVariant
}

@Preview
@Composable
private fun Preview(){
    Column{
        SwitchBar(title = "title", checked = true, summary = "summary"){}
        SwitchBar(title = "title", checked = false){}
    }
}