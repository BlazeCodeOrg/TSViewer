/*
 *
 *  * Copyright (c) BlazeCode / Ralf Lehmann, 2023.
 *
 */

package com.blazecode.eventtool.views

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
fun SliderPreference(
    title: String,
    icon: Painter? = null,
    value: Float = 0f,
    steps: Int = 0,
    valueRange: ClosedFloatingPointRange<Float> = 0f..1f,
    unitSuffix: String = "",
    onValueChange: (Float) -> Unit){
    val currentValue = remember { mutableStateOf(value) }

    Card (modifier = Modifier.padding(dimensionResource(R.dimen.medium_padding), dimensionResource(R.dimen.small_padding), dimensionResource(R.dimen.medium_padding), 0.dp)){
        Row (modifier = Modifier.fillMaxWidth().padding(dimensionResource(R.dimen.small_padding)), verticalAlignment = Alignment.CenterVertically){
            if(icon != null){
                Box (modifier = Modifier.size(dimensionResource(R.dimen.icon_button_size)).weight(1f), contentAlignment = Alignment.Center){
                    Icon(icon, "")
                }
            }
            Column (modifier = Modifier.weight(6f, true)){
                Row {
                    Text(title, color = MaterialTheme.colorScheme.onSurface, fontSize = 16.sp)
                    Spacer(modifier = Modifier.weight(1f))
                    Text("${currentValue.value.toInt()} $unitSuffix", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 15.sp)
                }
                Slider(
                    value = currentValue.value,
                    steps = steps,
                    valueRange = valueRange,
                    onValueChange = { onValueChange(it); currentValue.value = it })
            }
        }
    }
}

@Preview
@Composable
private fun Preview(){
    Column{
        SliderPreference(icon = painterResource(R.drawable.ic_settings), title = "title"){}
        SliderPreference(icon = painterResource(R.drawable.ic_settings), title = "title", steps = 10, value = .5f){}
    }
}
