/*
 *
 *  * Copyright (c) BlazeCode / Ralf Lehmann, 2022.
 *
 */

package com.blazecode.tsviewer.util.updater

data class GitHubRelease(
    val tag_name: String,
    val body: String,
    val assets: ArrayList<GitHubAssets>
)
