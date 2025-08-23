package com.glambiase.run.domain

import com.glambiase.core.domain.location.LocationWithAltitudeAndTimestamp

data class RunData(
    val distanceMeters: Int = 0,
    val locations: List<List<LocationWithAltitudeAndTimestamp>> = emptyList()
)