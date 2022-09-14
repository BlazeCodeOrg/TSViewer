/*
 *
 *  * Copyright (c) BlazeCode / Ralf Lehmann, 2022.
 *
 */

package com.blazecode.tsviewer.util.updater

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters

class UpdateCheckWorker(private val context: Context, workerParameters: WorkerParameters) :
    Worker(context, workerParameters) {

    val mContext = context

    override fun doWork(): Result {
        val gitHubUpdater = GitHubUpdater(mContext)
        gitHubUpdater.checkForUpdate()

        return Result.success()
    }
}