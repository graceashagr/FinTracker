package com.example.core.data.sync

import android.content.Context
import com.example.core.domain.sync.SyncScheduler
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class WorkManagerSyncScheduler @Inject constructor(
    @ApplicationContext private val context: Context
) : SyncScheduler {
    override fun triggerImmediateSync() {
        triggerImmediateSync(context)
    }
}
