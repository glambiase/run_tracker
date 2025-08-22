package com.glambiase.core.domain

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import kotlin.time.Duration.Companion.milliseconds

object Timer {
    fun elapsedTimeFlow() = flow {
        var lastEmittedTime = System.currentTimeMillis()
        while (true) {
            delay(500L)
            val currentTime = System.currentTimeMillis()
            val elapsedTime = currentTime - lastEmittedTime
            emit(elapsedTime.milliseconds)
            lastEmittedTime = currentTime
        }
    }
}