/*
 *
 *  * Copyright (c) BlazeCode / Ralf Lehmann, 2023.
 *
 */

package com.blazecode.tsviewer.util.updater

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.res.stringResource
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.blazecode.scrapguidev2.util.LinkUtil
import com.blazecode.tsviewer.BuildConfig
import com.blazecode.tsviewer.R
import com.blazecode.tsviewer.views.GitHubUpdateCard
import com.google.gson.GsonBuilder
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import timber.log.Timber

@Composable
fun GitHubUpdater(context: Context) {
    val release = remember{ mutableStateOf(GitHubRelease("","", arrayListOf())) }

    val download = rememberSaveable{ mutableStateOf(false) }
    val showDialog = rememberSaveable{ mutableStateOf(false) }
    val forceClose = rememberSaveable{ mutableStateOf(false) }

    fun parseJSON(input: String){
        val gson = GsonBuilder().create()
        val releases: Array<GitHubRelease> = gson.fromJson(input, Array<GitHubRelease>::class.java)
        val latestReleaseVersion = releases?.get(0)?.tag_name?.removePrefix("V")

        if(BuildConfig.VERSION_NAME.split("-")[0] != latestReleaseVersion){
            releases?.get(0)?.let { release.value = releases[0] }
            Timber.i("Update found")
        } else {
            Timber.i("No update found")
        }
    }

    fun checkForUpdate(){
        GlobalScope.launch {
            val queue = Volley.newRequestQueue(context)
            val url = context.resources.getString(R.string.github_releases_url)

            val stringRequest = StringRequest(
                Request.Method.GET, url,
                { response ->
                    parseJSON(response)
                },
                {
                })

            // Add the request to the RequestQueue.
            queue.add(stringRequest)
        }
    }

    LaunchedEffect(context){
        checkForUpdate()
    }

    if(release.value != GitHubRelease("","", arrayListOf()) && !forceClose.value){
        showDialog.value = true
    }

    AnimatedVisibility(showDialog.value){
        GitHubUpdateCard(
            title = stringResource(R.string.update_available, release.value.tag_name),
            description = release.value.body,
            onClickDownload = { download.value = true },
        )
    }

    if(download.value) {
        var asset: GitHubAssets? = null

        if(BuildConfig.FLAVOR == "core"){
            asset = release.value.assets.find { it.name.contains("core") }
        } else if (BuildConfig.FLAVOR == "foss"){
            asset = release.value.assets.find { it.name.contains("foss") }
        }

        if (asset != null) {
            LinkUtil.Builder(context).link(asset.browser_download_url).open()
        } else {
            Log.e("UPDATER", "No asset found, Flavor missing in name?")
            download.value = false
            showDialog.value = false
            forceClose.value = true
            Toast.makeText(context, "No asset found", Toast.LENGTH_SHORT).show()
        }
    }
}