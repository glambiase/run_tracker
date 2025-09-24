package com.glambiase.run.data

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.glambiase.core.database.dao.PendingRunSyncDao
import com.glambiase.core.database.mapper.toRun
import com.glambiase.core.domain.run.RemoteRunDataSource

class CreatedRunWorker(
    context: Context,
    private val params: WorkerParameters,
    private val remoteRunDataSource: RemoteRunDataSource,
    private val pendingRunSyncDao: PendingRunSyncDao
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {

        if (runAttemptCount >= 5) return Result.failure()

        val runId = params.inputData.getString(RUN_ID) ?: return Result.failure()

        val pendingCreatedRunEntity = pendingRunSyncDao.getPendingCreatedRunEntity(runId) ?: return Result.failure()

        val run = pendingCreatedRunEntity.run.toRun()
        return when (val result = remoteRunDataSource.postRun(run, pendingCreatedRunEntity.mapPictureBytes)) {
            is com.glambiase.core.domain.util.Result.Success -> {
                pendingRunSyncDao.deletePendingCreatedRunEntity(runId)
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