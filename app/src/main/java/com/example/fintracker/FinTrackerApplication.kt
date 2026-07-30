package com.example.fintracker

import android.app.Application
import android.util.Log
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import androidx.work.WorkManager
import com.example.core.data.sync.scheduleSyncWork
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class FinTrackerApplication: Application(), Configuration.Provider{

    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    override fun onCreate() {
        super.onCreate()
        Log.d("WM_DEBUG", "Using custom factory: ${workManagerConfiguration.workerFactory}")
        scheduleSyncWork(this)
        WorkManager.getInstance(this).getWorkInfosForUniqueWorkLiveData("sync_transactions")
            .observeForever { infos ->
                Log.d("WM_DEBUG", "Work state: ${infos.map { it.state }}")
            }
    }

    override fun getWorkManagerConfiguration(): Configuration {
       return Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()
    }

}
