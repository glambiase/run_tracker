package com.glambiase.core.data.di

import com.glambiase.core.data.auth.EncryptedSessionStorage
import com.glambiase.core.data.networking.HttpClientFactory
import com.glambiase.core.domain.SessionStorage
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val coreDataModule = module {
    single {
        HttpClientFactory(sessionStorage = get()).build()
    }

    singleOf(::EncryptedSessionStorage).bind<SessionStorage>()
}