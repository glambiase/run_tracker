package com.glambiase.run.domain

import com.glambiase.core.domain.location.LocationWithAltitudeAndTimestamp
import kotlin.time.Duration

data class RunData(
    val distanceMeters: Int = 0,
    val pace: Duration = Duration.ZERO,
    val locations: List<List<LocationWithAltitudeAndTimestamp>> = emptyList()
)