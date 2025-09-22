package com.glambiase.core.database.mapper

import com.glambiase.core.database.entity.RunEntity
import com.glambiase.core.domain.location.Location
import com.glambiase.core.domain.run.Run
import org.bson.types.ObjectId
import java.time.Instant
import java.time.ZoneId
import kotlin.time.Duration.Companion.milliseconds

fun RunEntity.toRun() =
    Run(
        id = id,
        duration = durationMillis.milliseconds,
        dateTimeUtc = Instant.parse(dateTimeUtc).atZone(ZoneId.of("UTC")),
        distanceMeters = distanceMeters,
        location = Location(latitude, longitude),
        maxSpeedKmH = maxSpeedKmH,
        totalElevationMeters = totalElevationMeters,
        mapPictureUrl = mapPictureUrl
    )

fun List<RunEntity>.toRuns() = map { it.toRun() }

fun Run.toRunEntity() =
    RunEntity(
        durationMillis = duration.inWholeMilliseconds,
        distanceMeters = distanceMeters,
        dateTimeUtc = dateTimeUtc.toInstant().toString(),
        latitude = location.lat,
        longitude = location.long,
        avgSpeedKmH = avgSpeedKmH,
        maxSpeedKmH = maxSpeedKmH,
        totalElevationMeters = totalElevationMeters,
        mapPictureUrl = mapPictureUrl,
        id = id ?: ObjectId().toHexString()
    )

fun List<Run>.toRunEntities() = map { it.toRunEntity() }