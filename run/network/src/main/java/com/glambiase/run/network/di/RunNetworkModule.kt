package com.glambiase.run.network.di

import com.glambiase.core.domain.run.RemoteRunDataSource
import com.glambiase.run.network.KtorRemoteRunDataSource
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val runNetworkModule = module {
    singleOf(::KtorRemoteRunDataSource).bind<RemoteRunDataSource>()
}