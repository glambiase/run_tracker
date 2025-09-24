package com.glambiase.run.data

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.glambiase.core.database.dao.PendingRunSyncDao
import com.glambiase.core.domain.run.RemoteRunDataSource

class DeletedRunWorker(
    context: Context,
    private val params: WorkerParameters,
    private val remoteRunDataSource: RemoteRunDataSource,
    private val pendingRunSyncDao: PendingRunSyncDao
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {

        if (runAttemptCount >= 5) return Result.failure()

        val runId = params.inputData.getString(RUN_ID) ?: return Result.failure()

        return when (val result = remoteRunDataSource.deleteRun(runId)) {
            is com.glambiase.core.domain.util.Result.Success -> {
                pendingRunSyncDao.deletePendingDeletedRunEntity(runId)
                Result.success()
            }
            is com.glambiase.core.domain.util.Result.Error -> {
                result.error.toWorkerResult()
            }
        }
    }

    companion object {
        const val RUN_ID = "RUN_ID"
    }
}