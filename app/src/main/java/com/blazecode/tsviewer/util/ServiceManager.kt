package com.blazecode.tsviewer.util

import android.app.Application
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequest
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.WorkManager
import androidx.work.WorkRequest
import com.google.common.util.concurrent.ListenableFuture
import util.ClientsWorker
import util.SettingsManager
import java.util.concurrent.ExecutionException
import java.util.concurrent.TimeUnit

class ServiceManager(val app: Application) {

    private val TAG = "scheduleClients"
    private var workManager: WorkManager = app.let { WorkManager.getInstance(it) }
    private val settingsManager = SettingsManager(app)

    fun startService(){
        val clientWorkRequest: PeriodicWorkRequest = PeriodicWorkRequestBuilder<ClientsWorker>(
            settingsManager.getScheduleTime().toLong(),                                                                                 //GIVE NEW WORK TIME
            TimeUnit.MINUTES,
            1, TimeUnit.MINUTES)                                                                      //FLEX TIME INTERVAL
            .build()

        val oneTimeclientWorkRequest: WorkRequest = OneTimeWorkRequestBuilder<ClientsWorker>().build()          //RUN ONE TIME
        workManager.enqueue(oneTimeclientWorkRequest)
        workManager.enqueueUniquePeriodicWork(TAG, ExistingPeriodicWorkPolicy.UPDATE, clientWorkRequest)     //SCHEDULE THE NEXT RUNS
    }

    fun stopService(){
        workManager.cancelUniqueWork(TAG)
    }

    fun isRunning(): Boolean {
        val instance = WorkManager.getInstance(app.applicationContext)
        val statuses: ListenableFuture<List<WorkInfo>> = instance.getWorkInfosForUniqueWork(TAG)
        return try {
            var running = false
            val workInfoList: List<WorkInfo> = statuses.get()
            for (workInfo in workInfoList) {
                val state = workInfo.state
                running = state == WorkInfo.State.RUNNING || state == WorkInfo.State.ENQUEUED
            }
            running
        } catch (e: ExecutionException) {
            e.printStackTrace()
            false
        } catch (e: InterruptedException) {
            e.printStackTrace()
            false
        }
    }
}