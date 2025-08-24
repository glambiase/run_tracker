package com.glambiase.run.presentation.active_run.maps

import androidx.compose.ui.graphics.Color
import com.glambiase.core.domain.location.Location

data class PolylineSegment(
    val locationA: Location,
    val locationB: Location,
    val color: Color
)