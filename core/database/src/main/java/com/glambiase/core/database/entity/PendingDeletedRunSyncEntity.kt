package com.glambiase.core.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class PendingDeletedRunSyncEntity(
    @PrimaryKey(autoGenerate = false)
    val runId: String,
    val userId: String
)