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
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.blazecode.tsviewer.R

@Composable
fun BigSwitchPreference(
    title: String,
    checked: Boolean,
    onCheckChanged: (Boolean) -> Unit){
    Card (modifier = Modifier.padding(dimensionResource(R.dimen.medium_padding), dimensionResource(R.dimen.small_padding), dimensionResource(R.dimen.medium_padding), 0.dp),
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceTint),){
        Row (modifier = Modifier.fillMaxWidth().padding(dimensionResource(R.dimen.medium_padding)), verticalAlignment = Alignment.CenterVertically){
            Column (modifier = Modifier.weight(5f, true)){
                Text(title, color = MaterialTheme.colorScheme.inverseOnSurface, fontSize = 18.sp)
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
        BigSwitchPreference(title = "title", checked = true){}
        BigSwitchPreference(title = "title", checked = false){}
    }
}