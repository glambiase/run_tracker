package com.glambiase.core.domain.location

import kotlin.time.Duration

data class LocationWithAltitudeAndTimestamp(
    val locationWithAltitude: LocationWithAltitude,
    val timestamp: Duration
)