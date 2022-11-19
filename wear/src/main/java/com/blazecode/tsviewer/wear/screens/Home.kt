/*
 *
 *  * Copyright (c) BlazeCode / Ralf Lehmann, 2022.
 *
 */

package com.blazecode.tsviewer.wear.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.wear.compose.material.Button
import androidx.wear.compose.material.Text
import com.blazecode.tsviewer.wear.complication.ComplicationDataHolder
import com.blazecode.tsviewer.wear.complication.ComplicationProvider

@Composable
fun Home() {
    val text = remember { mutableStateOf("") }
    val context = LocalContext.current
    BasicTextField(
        value = text.value,
        onValueChange = { text.value = it },
        modifier = Modifier.fillMaxWidth().background(color = Color.White),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
    )
    Button(onClick = {
        ComplicationDataHolder.amount = text.value.toInt()
        ComplicationProvider().update(context)
    }, modifier = Modifier.fillMaxWidth()) { Text("Send") }
}