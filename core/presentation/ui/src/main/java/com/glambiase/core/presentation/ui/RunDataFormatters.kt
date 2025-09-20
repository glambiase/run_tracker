package com.glambiase.core.presentation.ui

import kotlin.math.pow
import kotlin.math.round
import kotlin.math.roundToInt
import kotlin.time.Duration

fun Duration.formatted(): String {
    val totalSeconds = inWholeSeconds
    val hours = (totalSeconds / 3600).toString().padStart(2, '0')
    val minutes = ((totalSeconds % 3600) / 60).toString().padStart(2, '0')
    val seconds = (totalSeconds % 60).toString().padStart(2, '0')

    return "$hours:$minutes:$seconds"
}

fun Double.toFormattedKm() = "${roundToDecimalPlaces(decimalPlace = 1)} km"

fun Double.toFormattedKmH() = "${roundToDecimalPlaces(decimalPlace = 1)} km/h"

fun Duration.toFormattedPace(distanceKm: Double): String {
    if (this == Duration.ZERO || distanceKm <= 0.0) return "-"

    val secondsPerKm = (inWholeSeconds / distanceKm).roundToInt()
    val avgPaceMinutesPerKm = secondsPerKm / 60
    val avgPaceSecondsPerKm = (secondsPerKm % 60).toString().padStart(2, '0')

    return "$avgPaceMinutesPerKm:$avgPaceSecondsPerKm / km"
}

private fun Double.roundToDecimalPlaces(decimalPlace: Int): Double {
    val factor = 10f.pow(decimalPlace)
    return round(this * factor) / factor
}

fun Int.toFormattedMeters() = "$this m"