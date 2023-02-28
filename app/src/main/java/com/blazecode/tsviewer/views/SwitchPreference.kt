/*
 *
 *  * Copyright (c) BlazeCode / Ralf Lehmann, 2023.
 *
 */

package com.blazecode.eventtool.views

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.blazecode.tsviewer.R

@Composable
fun SwitchPreference(icon: Painter?, title: String, summary: String?, checked: Boolean, onCheckChanged: (Boolean) -> Unit){
    Card (modifier = Modifier.padding(dimensionResource(R.dimen.medium_padding), dimensionResource(R.dimen.medium_padding), dimensionResource(R.dimen.medium_padding), 0.dp)){
        Row (modifier = Modifier.fillMaxWidth().padding(dimensionResource(R.dimen.small_padding)), verticalAlignment = Alignment.CenterVertically){
            if(icon != null){
                Box (modifier = Modifier.size(dimensionResource(R.dimen.icon_button_size)).weight(1f), contentAlignment = Alignment.Center){
                    Icon(icon, "")
                }
            }
            Column (modifier = Modifier.weight(5f, true)){
                Text(title, color = MaterialTheme.colorScheme.onSurface, fontSize = 16.sp)
                if(!summary.isNullOrEmpty()) Text(summary, fontSize = 15.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Box (modifier = Modifier.fillMaxWidth().padding(0.dp, 0.dp, dimensionResource(R.dimen.medium_padding), 0.dp).weight(1.5f), contentAlignment = Alignment.CenterEnd){
                Switch(checked = checked, onCheckedChange = onCheckChanged)
            }
        }
    }
}

@Preview
@Composable
private fun Preview(){
    Column{
        SwitchPreference(painterResource(R.drawable.ic_settings), "title", null, true){}
        SwitchPreference(painterResource(R.drawable.ic_settings), "title", "summary", false){}
    }
}