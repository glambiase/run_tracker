package com.glambiase.core.domain.run

import kotlin.time.Duration

interface SyncRunScheduler {
    suspend fun scheduleSync(syncType: SyncType)
    suspend fun cancelAllSyncs()

    sealed interface SyncType {
        data class FetchRuns(val interval: Duration) : SyncType
        class CreatedRun(val run: Run, val mapPictureBytes: ByteArray) : SyncType
        data class DeletedRun(val runId: String) : SyncType
    }
}