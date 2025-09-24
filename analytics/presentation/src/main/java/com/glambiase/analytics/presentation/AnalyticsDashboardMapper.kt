package com.glambiase.analytics.presentation

import com.glambiase.analytics.domain.AnalyticsValues
import com.glambiase.core.presentation.ui.formatted
import com.glambiase.core.presentation.ui.toFormattedKm
import com.glambiase.core.presentation.ui.toFormattedKmH
import com.glambiase.core.presentation.ui.toFormattedTotalTime
import kotlin.time.Duration.Companion.seconds

fun AnalyticsValues.toAnalyticsDashboardState() =
    AnalyticsDashboardState(
        totalDistanceRun = (totalDistanceRun / 1000.0).toFormattedKm(),
        totalTimeRun = totalTimeRun.toFormattedTotalTime(),
        fastestEverRun = fastestEverRun.toFormattedKmH(),
        avgDistance = (avgDistancePerRun / 1000.0).toFormattedKm(),
        avgPace = avgPacePerRun.seconds.formatted()
    )