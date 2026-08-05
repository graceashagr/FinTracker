package com.example.core.data.sync

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.Constraints
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.example.core.domain.sync.SyncEngine
import com.example.core.domain.sync.SyncResult
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import java.util.concurrent.TimeUnit

@HiltWorker
class SyncWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val syncEngines: Set<@JvmSuppressWildcards SyncEngine>
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val results = syncEngines.map {
            it.sync()
        }
        return if(results.all { it is SyncResult.Success }) Result.success() else Result.retry()
    }
}