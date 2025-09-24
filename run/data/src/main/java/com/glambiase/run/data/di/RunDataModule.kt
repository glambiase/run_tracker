package com.glambiase.run.data.di

import com.glambiase.run.data.CreatedRunWorker
import com.glambiase.run.data.DeletedRunWorker
import com.glambiase.run.data.FetchRunsWorker
import org.koin.androidx.workmanager.dsl.workerOf
import org.koin.dsl.module

val runDataModule = module {
    workerOf(::FetchRunsWorker)
    workerOf(::CreatedRunWorker)
    workerOf(::DeletedRunWorker)
}