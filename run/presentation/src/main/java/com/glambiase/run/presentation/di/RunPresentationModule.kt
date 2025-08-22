package com.glambiase.run.presentation.di

import com.glambiase.run.presentation.active_run.ActiveRunViewModel
import com.glambiase.run.presentation.run_overview.RunOverviewViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val runPresentationModule = module {
    viewModelOf(::RunOverviewViewModel)
    viewModelOf(::ActiveRunViewModel)
}