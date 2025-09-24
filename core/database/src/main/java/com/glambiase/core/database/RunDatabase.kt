package com.glambiase.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.glambiase.core.database.dao.RunDao
import com.glambiase.core.database.dao.PendingRunSyncDao
import com.glambiase.core.database.entity.PendingDeletedRunSyncEntity
import com.glambiase.core.database.entity.RunEntity
import com.glambiase.core.database.entity.PendingCreatedRunSyncEntity

@Database(entities = [RunEntity::class, PendingCreatedRunSyncEntity::class, PendingDeletedRunSyncEntity::class], version = 1)
abstract class RunDatabase : RoomDatabase() {

    abstract val runDao: RunDao
    abstract val pendingRunSyncDao: PendingRunSyncDao
}