package com.glambise.analytics.data.di

import com.glambiase.analytics.domain.AnalyticsRepository
import com.glambiase.core.database.RunDatabase
import com.glambise.analytics.data.RoomAnalyticsRepository
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val analyticsDataModule = module {
    singleOf(::RoomAnalyticsRepository).bind<AnalyticsRepository>()
    single {
        get<RunDatabase>().analyticsDao
    }
}