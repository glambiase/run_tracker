package com.glambiase.run.domain

import com.glambiase.core.domain.location.LocationWithAltitudeAndTimestamp
import kotlin.math.roundToInt
import kotlin.time.DurationUnit

object LocationDataCalculator {

    fun getTotalDistanceMeters(locations: List<List<LocationWithAltitudeAndTimestamp>>) =
        locations.sumOf { innerList ->
            innerList.zipWithNext { pointA, pointB ->
                pointA.locationWithAltitude.location.distanceMetersTo(pointB.locationWithAltitude.location)
            }.sum().roundToInt()
        }

    fun getMaxSpeedKmH(locations: List<List<LocationWithAltitudeAndTimestamp>>) =
        locations.maxOf { innerList ->
            innerList.zipWithNext { pointA, pointB ->
                val km = pointA.locationWithAltitude.location.distanceMetersTo(pointB.locationWithAltitude.location) / 1000.0
                val hours = (pointB.timestamp - pointA.timestamp).toDouble(DurationUnit.HOURS)
                if (hours == 0.0) 0.0 else km / hours
            }.maxOrNull() ?: 0.0
        }

    fun getTotalElevationMeters(locations: List<List<LocationWithAltitudeAndTimestamp>>) =
        locations.sumOf { innerList ->
            innerList.zipWithNext { pointA, pointB ->
                val altitudeA = pointA.locationWithAltitude.altitude
                val altitudeB = pointB.locationWithAltitude.altitude
                (altitudeB - altitudeA).coerceAtLeast(0.0)
            }.sum().roundToInt()
        }
}