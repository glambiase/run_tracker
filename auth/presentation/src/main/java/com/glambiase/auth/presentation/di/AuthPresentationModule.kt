package com.glambiase.auth.presentation.di

import com.glambiase.auth.presentation.registration.RegistrationViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val authPresentationModule = module {
    viewModelOf(::RegistrationViewModel)
}