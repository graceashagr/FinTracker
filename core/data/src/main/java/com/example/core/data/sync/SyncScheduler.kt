package com.example.core.data.sync

import android.content.Context
import android.util.Log
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import java.util.concurrent.TimeUnit

// core/data/sync/SyncScheduler.kt

fun scheduleSyncWork(context: Context) {
    Log.d("SyncScheduler", "scheduleSyncWork called")
    val constraints = Constraints.Builder()
        .setRequiredNetworkType(NetworkType.CONNECTED)
        .build()

    val periodicRequest = PeriodicWorkRequestBuilder<SyncWorker>(15, TimeUnit.MINUTES)
        .setConstraints(constraints)
        .build()

    WorkManager.getInstance(context).enqueueUniquePeriodicWork(
        "sync_action",
        ExistingPeriodicWorkPolicy.KEEP,
        periodicRequest
    )
}

fun triggerImmediateSync(context: Context) {
    Log.d("SyncScheduler", "triggerImmediateSync called")
    val constraints = Constraints.Builder()
        .setRequiredNetworkType(NetworkType.CONNECTED)
        .build()

    val request = OneTimeWorkRequestBuilder<SyncWorker>()
        .setConstraints(constraints)
        .build()

    WorkManager.getInstance(context).enqueueUniqueWork(
        "sync_action_immediate",
        ExistingWorkPolicy.KEEP,
        request
    )
}