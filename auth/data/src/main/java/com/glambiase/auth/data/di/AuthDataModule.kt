package com.glambiase.auth.data.di

import com.glambiase.auth.data.EmailPatternValidator
import com.glambiase.auth.domain.PatternValidator
import com.glambiase.auth.domain.UserDataValidator
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val authDataModule = module {
    single<PatternValidator> {
        EmailPatternValidator
    }
    singleOf(::UserDataValidator)
}