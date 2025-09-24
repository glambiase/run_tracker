package com.glambiase.run.data.di

import com.glambiase.core.domain.run.SyncRunScheduler
import com.glambiase.run.data.CreatedRunWorker
import com.glambiase.run.data.DeletedRunWorker
import com.glambiase.run.data.FetchRunsWorker
import com.glambiase.run.data.SyncRunWorkerScheduler
import org.koin.androidx.workmanager.dsl.workerOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val runDataModule = module {
    workerOf(::FetchRunsWorker)
    workerOf(::CreatedRunWorker)
    workerOf(::DeletedRunWorker)

    singleOf(::SyncRunWorkerScheduler).bind<SyncRunScheduler>()
}