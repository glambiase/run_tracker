package com.glambiase.core.data.run

import com.glambiase.core.database.dao.PendingRunSyncDao
import com.glambiase.core.database.mapper.toRun
import com.glambiase.core.domain.SessionStorage
import com.glambiase.core.domain.run.LocalRunDataSource
import com.glambiase.core.domain.run.RemoteRunDataSource
import com.glambiase.core.domain.run.Run
import com.glambiase.core.domain.run.RunId
import com.glambiase.core.domain.run.RunRepository
import com.glambiase.core.domain.util.DataError
import com.glambiase.core.domain.util.EmptyResult
import com.glambiase.core.domain.util.Result
import com.glambiase.core.domain.util.asEmptyResult
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class OfflineFirstRunRepository(
    private val localRunDataSource: LocalRunDataSource,
    private val remoteRunDataSource: RemoteRunDataSource,
    private val pendingRunSyncDao: PendingRunSyncDao,
    private val sessionStorage: SessionStorage,
    private val applicationScope: CoroutineScope
) : RunRepository {
    override fun getRuns(): Flow<List<Run>> = localRunDataSource.getRuns()

    override suspend fun fetchRuns(): EmptyResult<DataError> =
        when (val result = remoteRunDataSource.getRuns()) {
            is Result.Error -> result.asEmptyResult()
            is Result.Success -> applicationScope.async {
                localRunDataSource.upsertRuns(runs = result.data).asEmptyResult()
            }.await()
        }

    override suspend fun upsertRun(run: Run, mapPicture: ByteArray): EmptyResult<DataError> {
        val localResult = localRunDataSource.upsertRun(run)
        if (localResult !is Result.Success) return localResult.asEmptyResult()

        val runWithId = run.copy(id = localResult.data)
        val remoteResult = remoteRunDataSource.postRun(
            run = runWithId,
            mapPicture = mapPicture
        )
        return when (remoteResult) {
            is Result.Error -> Result.Success(Unit) // TODO: to be handle appropriately
            is Result.Success -> applicationScope.async {
                localRunDataSource.upsertRun(run = remoteResult.data).asEmptyResult()
            }.await()
        }
    }

    override suspend fun deleteRun(id: RunId) {
        localRunDataSource.deleteRun(id)

        // Edge case where the run is created in offline-mode, and then also deleted in offline-mode.
        // In that case, we don't need to sync anything with the backend.
        val isPendingCreatedRun = pendingRunSyncDao.getPendingCreatedRunEntity(id) != null
        if (isPendingCreatedRun) {
            pendingRunSyncDao.deletePendingCreatedRunEntity(id)
            return
        }

        val remoteResult = applicationScope.async {
            remoteRunDataSource.deleteRun(id)
        }.await()
    }

    override suspend fun deleteAllRuns() = localRunDataSource.deleteAllRuns()

    override suspend fun syncPendingRuns() {
        withContext(Dispatchers.IO) {
            val userId = sessionStorage.get()?.userId ?: return@withContext
            val createdRuns = async {
                pendingRunSyncDao.getAllPendingCreatedRunEntities(userId)
            }
            val deletedRuns = async {
                pendingRunSyncDao.getAllPendingDeletedRunEntities(userId)
            }

            val createdRunsJobs = createdRuns
                .await()
                .map {
                    launch {
                        val run = it.run.toRun()
                        when (remoteRunDataSource.postRun(run, it.mapPictureBytes)) {
                            is Result.Success -> {
                                applicationScope.launch {
                                    pendingRunSyncDao.deletePendingCreatedRunEntity(it.runId)
                                }.join()
                            }
                            is Result.Error -> Unit
                        }
                    }
                }
            val deletedRunsJobs = deletedRuns
                .await()
                .map {
                    launch {
                        when (remoteRunDataSource.deleteRun(it.runId)) {
                            is Result.Success -> {
                                applicationScope.launch {
                                    pendingRunSyncDao.deletePendingDeletedRunEntity(it.runId)
                                }.join()
                            }
                            is Result.Error -> Unit
                        }
                    }
                }

            createdRunsJobs.forEach { it.join() }
            deletedRunsJobs.forEach { it.join() }
        }
    }
}