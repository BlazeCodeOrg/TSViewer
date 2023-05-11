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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.blazecode.tsviewer.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditTextPreference(
    modifier: Modifier = Modifier,
    title: String,
    text: String? = null,
    placeholder: String? = null,
    icon: Painter? = null,
    showSummary: Boolean? = true,
    isPassword: Boolean? = false,
    isNumber: Boolean? = false,
    singleLine: Boolean = false,
    onTextChange: (String) -> Unit){

    val isDialogVisible = remember { mutableStateOf(false) }
    val prefilledText = text ?: ""

    Box(modifier = modifier){
        Card (modifier = Modifier.padding(dimensionResource(R.dimen.medium_padding), dimensionResource(R.dimen.small_padding), dimensionResource(R.dimen.medium_padding), 0.dp).clickable(onClick = {isDialogVisible.value = true} )){
            Row (modifier = Modifier.fillMaxWidth().padding(dimensionResource(R.dimen.small_padding)), verticalAlignment = Alignment.CenterVertically){
                if(icon != null){
                    Box (modifier = Modifier.size(dimensionResource(R.dimen.icon_button_size)).weight(1f), contentAlignment = Alignment.Center){
                        Icon(icon, "")
                    }
                }
                Column (modifier = Modifier.weight(6f, true)){
                    Text(title, color = MaterialTheme.colorScheme.onSurface, fontSize = 16.sp)
                    if(showSummary == true){
                        var summaryText =
                            if (isPassword == true && prefilledText != "") {
                                prefilledText.replace(Regex("."), "\u2022")
                            } else if (isPassword == true && prefilledText == "" && placeholder != null) {
                                placeholder.replace(Regex("."), "\u2022")
                            } else if(text == "" && placeholder != null){
                                placeholder
                            } else
                                prefilledText

                        Text(
                            text = summaryText,
                            fontSize = 15.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        }
    }

    if (isDialogVisible.value) {
        val tempTextValue = remember { mutableStateOf(TextFieldValue(prefilledText, selection = TextRange(prefilledText.length))) }
        val passwordVisible = remember { mutableStateOf(false) }
        val focusRequester = remember { FocusRequester() }
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
                       value = tempTextValue.value,
                       singleLine = singleLine,
                       keyboardOptions = keyboardType,
                       placeholder = { Text(placeholder ?: "") },
                       visualTransformation = if (passwordVisible.value || isPassword == false) VisualTransformation.None else PasswordVisualTransformation(),
                       onValueChange = { tempTextValue.value = it },
                       modifier = Modifier.focusRequester(focusRequester),
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
            confirmButton = { TextButton(onClick = { isDialogVisible.value = false; tempTextValue.value.text.trim().also(onTextChange) }) { Text(stringResource(R.string.confirm)) } },
            dismissButton = { TextButton(onClick = { isDialogVisible.value = false }) { Text(stringResource(R.string.cancel)) } },
        )

        LaunchedEffect(Unit) {
            focusRequester.requestFocus()
        }
    }
}

@Preview
@Composable
private fun Preview(){
    Column{
        EditTextPreference(icon = painterResource(R.drawable.ic_settings), title = "title", showSummary = true, text = "test"){}
        EditTextPreference(icon = painterResource(R.drawable.ic_settings), title = "title", showSummary = false, text = "test"){}
    }
}
