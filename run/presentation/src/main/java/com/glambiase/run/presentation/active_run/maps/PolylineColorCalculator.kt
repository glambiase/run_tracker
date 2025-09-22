package com.glambiase.run.presentation.active_run.maps

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.core.graphics.ColorUtils
import com.glambiase.core.domain.location.LocationWithAltitudeAndTimestamp
import kotlin.math.abs

object PolylineColorCalculator {

    fun getSpeedBasedColor(
        locationA: LocationWithAltitudeAndTimestamp,
        locationB: LocationWithAltitudeAndTimestamp
    ): Color {
        val distanceMeters = locationA.locationWithAltitude.location.distanceMetersTo(locationB.locationWithAltitude.location)
        val timeDiff = abs((locationB.timestamp - locationA.timestamp).inWholeSeconds)
        val speedKmHour = (distanceMeters / timeDiff) * 3.6

        return interpolateColor(
            speedKmHour = speedKmHour,
            minSpeed = 5.0,
            maxSpeed = 20.0,
            colorStart = Color.Green,
            colorMid = Color.Blue,
            colorEnd = Color.Red
        )
    }

    private fun interpolateColor(
        speedKmHour: Double,
        minSpeed: Double,
        maxSpeed: Double,
        colorStart: Color,
        colorMid: Color,
        colorEnd: Color
    ): Color {
        val ratio = ((speedKmHour - minSpeed) / (maxSpeed - minSpeed)).coerceIn(0.0..1.0)
        val colorInt = if (ratio <= 0.5) {
            val startToMidRatio = ratio / 0.5
            ColorUtils.blendARGB(colorStart.toArgb(), colorMid.toArgb(), startToMidRatio.toFloat())
        } else {
            val midToEndRatio = (ratio - 0.5) / 0.5
            ColorUtils.blendARGB(colorMid.toArgb(), colorEnd.toArgb(), midToEndRatio.toFloat())
        }

        return Color(colorInt)
    }
}