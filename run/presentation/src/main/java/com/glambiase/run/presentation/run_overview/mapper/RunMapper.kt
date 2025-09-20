package com.glambiase.run.presentation.run_overview.mapper

import com.glambiase.core.domain.run.Run
import com.glambiase.core.presentation.ui.formatted
import com.glambiase.core.presentation.ui.toFormattedKm
import com.glambiase.core.presentation.ui.toFormattedKmH
import com.glambiase.core.presentation.ui.toFormattedMeters
import com.glambiase.core.presentation.ui.toFormattedPace
import com.glambiase.run.presentation.run_overview.model.RunUi
import java.time.ZoneId
import java.time.format.DateTimeFormatter

fun Run.toRunUI(): RunUi {

    val dateTimeInLocalTime = dateTimeUtc.withZoneSameInstant(ZoneId.systemDefault())
    val formattedDateTime = DateTimeFormatter
        .ofPattern("MMM dd, yyy - hh:mma")
        .format(dateTimeInLocalTime)
    val distanceKm = distanceMeters / 1000.0

    return RunUi(
        id = id.orEmpty(),
        duration = duration.formatted(),
        dateTime = formattedDateTime,
        distance = distanceKm.toFormattedKm(),
        avgSpeed = avgSpeedKmH.toFormattedKmH(),
        maxSpeed = maxSpeedKmH.toFormattedKmH(),
        pace = duration.toFormattedPace(distanceKm),
        totalElevation = totalElevationMeters.toFormattedMeters(),
        mapPictureUrl = mapPictureUrl
    )
}