package com.glambiase.run.data

import android.content.Context
import androidx.work.BackoffPolicy
import androidx.work.Constraints
import androidx.work.Data
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.await
import com.glambiase.core.database.dao.PendingRunSyncDao
import com.glambiase.core.database.entity.PendingCreatedRunSyncEntity
import com.glambiase.core.database.entity.PendingDeletedRunSyncEntity
import com.glambiase.core.database.mapper.toRunEntity
import com.glambiase.core.domain.SessionStorage
import com.glambiase.core.domain.run.Run
import com.glambiase.core.domain.run.SyncRunScheduler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.concurrent.TimeUnit
import kotlin.time.Duration
import kotlin.time.toJavaDuration

class SyncRunWorkerScheduler(
    context: Context,
    private val pendingRunSyncDao: PendingRunSyncDao,
    private val sessionStorage: SessionStorage,
    private val applicationScope: CoroutineScope
) : SyncRunScheduler {

    private val workManager = WorkManager.getInstance(context)
    override suspend fun scheduleSync(syncType: SyncRunScheduler.SyncType) {
        when (syncType) {
            is SyncRunScheduler.SyncType.FetchRuns -> scheduleFetchRunsWorker(interval = syncType.interval)
            is SyncRunScheduler.SyncType.CreatedRun -> scheduleCreatedRunWorker(run = syncType.run, mapPictureBytes = syncType.mapPictureBytes)
            is SyncRunScheduler.SyncType.DeletedRun -> scheduleDeletedRunWorker(runId = syncType.runId)
        }
    }

    private suspend fun scheduleFetchRunsWorker(interval: Duration) {

        val isSyncScheduled = withContext(Dispatchers.IO) {
            workManager
                .getWorkInfosByTag(SYNC_WORK_TAG)
                .get()
                .isNotEmpty()
        }
        if (isSyncScheduled) return

        val workRequest = PeriodicWorkRequestBuilder<FetchRunsWorker>(repeatInterval = interval.toJavaDuration())
            .setConstraints(
                Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .build()
            )
            .setBackoffCriteria(
                backoffPolicy = BackoffPolicy.EXPONENTIAL,
                backoffDelay = 2000L,
                timeUnit = TimeUnit.MILLISECONDS
            )
            .setInitialDelay(
                duration = 30,
                timeUnit = TimeUnit.MINUTES
            )
            .addTag(SYNC_WORK_TAG)
            .build()

        workManager.enqueue(workRequest).await()
    }

    private suspend fun scheduleCreatedRunWorker(run: Run, mapPictureBytes: ByteArray) {

        val userId = sessionStorage.get()?.userId ?: return

        val pendingCreatedRun = PendingCreatedRunSyncEntity(
            run = run.toRunEntity(),
            mapPictureBytes = mapPictureBytes,
            userId = userId
        )
        pendingRunSyncDao.upsertPendingCreatedRunEntity(pendingCreatedRun)

        val workerRequest = OneTimeWorkRequestBuilder<CreatedRunWorker>()
            .setConstraints(
                Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .build()
            )
            .setBackoffCriteria(
                backoffPolicy = BackoffPolicy.EXPONENTIAL,
                backoffDelay = 2000L,
                timeUnit = TimeUnit.MILLISECONDS
            )
            .setInputData(
                Data.Builder()
                    .putString(key = CreatedRunWorker.RUN_ID, value = pendingCreatedRun.runId)
                    .build()
            )
            .addTag(CREATED_WORK_TAG)
            .build()

        applicationScope.launch {
            workManager.enqueue(workerRequest).await()
        }.join()
    }

    private suspend fun scheduleDeletedRunWorker(runId: String) {

        val userId = sessionStorage.get()?.userId ?: return

        val pendingDeletedRun = PendingDeletedRunSyncEntity(
            runId = runId,
            userId = userId
        )
        pendingRunSyncDao.upsertPendingDeletedRunEntity(pendingDeletedRun)

        val workRequest = OneTimeWorkRequestBuilder<DeletedRunWorker>()
            .setConstraints(
                Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .build()
            )
            .setBackoffCriteria(
                backoffPolicy = BackoffPolicy.EXPONENTIAL,
                backoffDelay = 2000L,
                timeUnit = TimeUnit.MILLISECONDS
            )
            .setInputData(
                Data.Builder()
                    .putString(key = DeletedRunWorker.RUN_ID, value = pendingDeletedRun.runId)
                    .build()
            )
            .addTag(DELETED_WORK_TAG)
            .build()

        applicationScope.launch {
            workManager.enqueue(workRequest).await()
        }.join()
    }

    override suspend fun cancelAllSyncs() {
        workManager.cancelAllWork().await()
    }

    companion object {
        private const val SYNC_WORK_TAG = "sync_work"
        private const val CREATED_WORK_TAG = "created_work"
        private const val DELETED_WORK_TAG = "deleted_work"
    }
}