package com.glambiase.run_tracker

import kotlinx.serialization.Serializable

sealed interface Routes {

    /*** auth_graph ***/
    @Serializable
    data object Auth: Routes

    @Serializable
    data object Intro : Routes

    @Serializable
    data object Registration : Routes

    @Serializable
    data object Login : Routes
    /*** ***/

    /*** run_graph ***/
    @Serializable
    data object Run : Routes

    @Serializable
    data object RunOverview : Routes

    @Serializable
    data object ActiveRun : Routes
    /*** ***/
}