package com.glambiase.run.domain

import com.glambiase.core.domain.location.LocationWithAltitudeAndTimestamp
import kotlin.math.roundToInt

object LocationDataCalculator {

    fun getTotalDistanceMeters(locations: List<List<LocationWithAltitudeAndTimestamp>>) =
        locations.sumOf { innerList ->
            innerList.zipWithNext { pointA, pointB ->
                pointA.locationWithAltitude.location.distanceTo(pointB.locationWithAltitude.location)
            }.sum().roundToInt()
        }
}