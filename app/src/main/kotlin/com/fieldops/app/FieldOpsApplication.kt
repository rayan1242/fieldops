package com.fieldops.app

import android.app.Application
import androidx.work.*
import com.fieldops.app.worker.SyncIncidentsWorker
import dagger.hilt.android.HiltAndroidApp
import java.util.concurrent.TimeUnit

@HiltAndroidApp
class FieldOpsApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        scheduleSyncWorker()
    }

    private fun scheduleSyncWorker() {
        val syncRequest = PeriodicWorkRequestBuilder<SyncIncidentsWorker>(
            repeatInterval = 15,
            repeatIntervalTimeUnit = TimeUnit.MINUTES
        )
            .setConstraints(
                Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .build()
            )
            .build()

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "sync_incidents",
            ExistingPeriodicWorkPolicy.KEEP,
            syncRequest
        )
    }
}
