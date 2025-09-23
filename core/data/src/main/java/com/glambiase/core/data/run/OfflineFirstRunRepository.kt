package com.glambiase.core.data.run

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
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.Flow

class OfflineFirstRunRepository(
    private val localRunDataSource: LocalRunDataSource,
    private val remoteRunDataSource: RemoteRunDataSource,
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

        val remoteResult = applicationScope.async {
            remoteRunDataSource.deleteRun(id)
        }.await()
    }

    override suspend fun deleteAllRuns() = localRunDataSource.deleteAllRuns()
}