package com.glambiase.run.network

import com.glambiase.core.domain.location.Location
import com.glambiase.core.domain.run.Run
import java.time.Instant
import java.time.ZoneId
import kotlin.time.Duration.Companion.milliseconds

fun RunDto.toRun() =
    Run(
        id = id,
        duration = durationMillis.milliseconds,
        dateTimeUtc = Instant.parse(dateTimeUtc).atZone(ZoneId.of("UTC")),
        distanceMeters = distanceMeters,
        location = Location(lat, long),
        maxSpeedKmH = maxSpeedKmh,
        totalElevationMeters = totalElevationMeters,
        mapPictureUrl = mapPictureUrl
    )

fun Run.toCreateRunRequest() =
    CreateRunRequest(
        durationMillis = duration.inWholeMilliseconds,
        distanceMeters = distanceMeters,
        epochMillis = dateTimeUtc.toEpochSecond() * 1000L,
        lat = location.lat,
        long = location.long,
        avgSpeedKmh = avgSpeedKmH,
        maxSpeedKmh = maxSpeedKmH,
        totalElevationMeters = totalElevationMeters,
        id = id.orEmpty()
    )