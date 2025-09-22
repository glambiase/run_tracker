package com.glambiase.core.database

import android.database.sqlite.SQLiteFullException
import com.glambiase.core.database.dao.RunDao
import com.glambiase.core.database.mapper.toRunEntities
import com.glambiase.core.database.mapper.toRunEntity
import com.glambiase.core.database.mapper.toRuns
import com.glambiase.core.domain.run.LocalRunDataSource
import com.glambiase.core.domain.run.Run
import com.glambiase.core.domain.run.RunId
import com.glambiase.core.domain.util.DataError
import com.glambiase.core.domain.util.Result
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class RoomLocalRunDataSource(
    private val runDao: RunDao
) : LocalRunDataSource {

    override fun getRuns(): Flow<List<Run>> = runDao.getRuns().map { it.toRuns() }

    override suspend fun upsertRun(run: Run): Result<RunId, DataError.Local> =
        try {
            val runEntity = run.toRunEntity()
            runDao.upsertRun(runEntity)
            Result.Success(data = runEntity.id)
        } catch (e: SQLiteFullException) {
            Result.Error(error = DataError.Local.DISK_FULL)
        }

    override suspend fun upsertRuns(runs: List<Run>): Result<List<RunId>, DataError.Local> =
        try {
            val runEntities = runs.toRunEntities()
            runDao.upsertRuns(runEntities)
            Result.Success(data = runEntities.map { it.id })
        } catch (e: SQLiteFullException) {
            Result.Error(error = DataError.Local.DISK_FULL)
        }

    override suspend fun deleteRun(id: RunId) = runDao.deleteRun(id)

    override suspend fun deleteAllRuns() = runDao.deleteAllRuns()
}