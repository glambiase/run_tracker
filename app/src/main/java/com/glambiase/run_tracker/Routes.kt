package com.glambiase.run_tracker

import kotlinx.serialization.Serializable

sealed interface Routes {

    @Serializable
    data object Intro : Routes

    @Serializable
    data object Auth: Routes

    @Serializable
    data object Registration: Routes

    @Serializable
    data object Login: Routes
}