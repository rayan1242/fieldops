package com.fieldops.app.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.fieldops.app.data.local.FieldOpsDatabase
import com.fieldops.app.data.remote.api.RetrofitInstance
import com.fieldops.app.data.repository.IncidentRepository

class SyncIncidentsWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val database = FieldOpsDatabase.getDatabase(applicationContext)
        val apiService = RetrofitInstance.apiService
        val repository = IncidentRepository(
            incidentDao = database.incidentDao(),
            apiService = apiService
        )

        return try {
            repository.syncOfflineIncidents()
            Result.success()
        } catch (e: Exception) {
            if (runAttemptCount < 3) Result.retry() else Result.failure()
        }
    }
}
