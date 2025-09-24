package com.glambiase.core.database.dao

import androidx.room.Query
import androidx.room.Upsert
import com.glambiase.core.database.entity.PendingDeletedRunSyncEntity
import com.glambiase.core.database.entity.PendingCreatedRunSyncEntity

interface PendingRunSyncDao {

    @Query("SELECT * FROM pendingcreatedrunsyncentity WHERE userId=:userId")
    suspend fun getAllPendingCreatedRunEntities(userId: String): List<PendingCreatedRunSyncEntity>

    @Query("SELECT * FROM pendingcreatedrunsyncentity WHERE runId=:runId")
    suspend fun getPendingCreatedRunEntity(runId: String): PendingCreatedRunSyncEntity?

    @Upsert
    suspend fun upsertPendingCreatedRunEntity(entity: PendingCreatedRunSyncEntity)

    @Query("DELETE FROM pendingcreatedrunsyncentity WHERE runId=:runId")
    suspend fun deletePendingCreatedRunEntity(runId: String)

    @Query("SELECT * FROM pendingdeletedrunsyncentity WHERE userId=:userId")
    suspend fun getAllPendingDeletedRunEntities(userId: String): List<PendingDeletedRunSyncEntity>

    @Upsert
    suspend fun upsertPendingDeletedRunEntity(entity: PendingDeletedRunSyncEntity)

    @Query("DELETE FROM pendingdeletedrunsyncentity WHERE runId=:runId")
    suspend fun deletePendingDeletedRunEntity(runId: String)
}