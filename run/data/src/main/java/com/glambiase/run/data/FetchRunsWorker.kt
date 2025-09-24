package com.glambiase.run.data

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.glambiase.core.domain.run.RunRepository

class FetchRunsWorker(
    context: Context,
    params: WorkerParameters,
    private val runRepository: RunRepository
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result =
        if (runAttemptCount >= 5) {
            Result.failure()
        } else {
            when (val result = runRepository.fetchRuns()) {
                is com.glambiase.core.domain.util.Result.Success -> Result.success()
                is com.glambiase.core.domain.util.Result.Error -> result.error.toWorkerResult()
            }
        }
}