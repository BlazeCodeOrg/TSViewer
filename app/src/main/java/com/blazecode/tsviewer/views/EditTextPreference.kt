/*
 *
 *  * Copyright (c) BlazeCode / Ralf Lehmann, 2023.
 *
 */

package com.blazecode.eventtool.views

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.blazecode.tsviewer.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditTextPreference(
    title: String,
    text: String? = null,
    icon: Painter? = null,
    useSimpleSummaryProvider: Boolean? = true,
    isPassword: Boolean? = false,
    isNumber: Boolean? = false,
    singleLine: Boolean = false,
    onTextChange: (String) -> Unit){

    val isDialogVisible = remember { mutableStateOf(false) }
    val prefilledText = text ?: ""

    Card (modifier = Modifier.padding(dimensionResource(R.dimen.medium_padding), dimensionResource(R.dimen.small_padding), dimensionResource(R.dimen.medium_padding), 0.dp).clickable(onClick = {isDialogVisible.value = true} )){
        Row (modifier = Modifier.fillMaxWidth().padding(dimensionResource(R.dimen.small_padding)), verticalAlignment = Alignment.CenterVertically){
            if(icon != null){
                Box (modifier = Modifier.size(dimensionResource(R.dimen.icon_button_size)).weight(1f), contentAlignment = Alignment.Center){
                    Icon(icon, "")
                }
            }
            Column (modifier = Modifier.weight(6f, true)){
                Text(title, color = MaterialTheme.colorScheme.onSurface, fontSize = 16.sp)
                if(useSimpleSummaryProvider == true)
                    Text(
                        text = if(isPassword == true) prefilledText.replace(Regex("."), "\u2022") else prefilledText,
                        fontSize = 15.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }

    if (isDialogVisible.value) {
        val tempText = remember { mutableStateOf(prefilledText) }
        val passwordVisible = remember { mutableStateOf(false) }
        val keyboardType =
            if (isPassword == true)
                KeyboardOptions(keyboardType = KeyboardType.Password)
            else if (isNumber == true)
                KeyboardOptions(keyboardType = KeyboardType.Number)
            else KeyboardOptions(keyboardType = KeyboardType.Text)

        AlertDialog(
            onDismissRequest = { isDialogVisible.value = false },
            title = { Text(title) },
            text = {
                   OutlinedTextField(
                       value = tempText.value,
                       singleLine = singleLine,
                       keyboardOptions = keyboardType,
                       visualTransformation = if (passwordVisible.value || isPassword == false) VisualTransformation.None else PasswordVisualTransformation(),
                       onValueChange = { tempText.value = it },
                       trailingIcon = {
                           if(isPassword == true){
                               val image = if (passwordVisible.value) painterResource(R.drawable.ic_visibility)
                               else painterResource(R.drawable.ic_visibility_off)

                               val description = if (passwordVisible.value) "Hide password" else "Show password"

                               IconButton(onClick = {passwordVisible.value = !passwordVisible.value}){
                                   Icon(painter = image, description)
                               }
                           }
                       }
                   )
           },
            confirmButton = { TextButton(onClick = { isDialogVisible.value = false; tempText.value.also(onTextChange) }) { Text(stringResource(R.string.confirm)) } },
            dismissButton = { TextButton(onClick = { isDialogVisible.value = false }) { Text(stringResource(R.string.cancel)) } },
        )
    }
}

@Preview
@Composable
private fun Preview(){
    Column{
        EditTextPreference(icon = painterResource(R.drawable.ic_settings), title = "title", useSimpleSummaryProvider = true, text = "test"){}
        EditTextPreference(icon = painterResource(R.drawable.ic_settings), title = "title", useSimpleSummaryProvider = false, text = "test"){}
    }
}
