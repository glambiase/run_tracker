package com.glambiase.analytics.presentation.di

import com.glambiase.analytics.presentation.AnalyticsDashboardViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val analyticsPresentationModule = module {
    viewModelOf(::AnalyticsDashboardViewModel)
}