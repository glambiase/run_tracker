package com.glambiase.run.presentation.active_run.maps

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.glambiase.core.domain.location.LocationWithAltitudeAndTimestamp
import com.google.android.gms.maps.model.JointType
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.Polyline

@Composable
fun RunTrackerPolyline(
    locations: List<List<LocationWithAltitudeAndTimestamp>>
) {
    val polylineSegments = remember(locations) {
        locations.map { innerList ->
            innerList.zipWithNext { pointA, pointB ->
                PolylineSegment(
                    locationA = pointA.locationWithAltitude.location,
                    locationB = pointB.locationWithAltitude.location,
                    color = PolylineColorCalculator.getSpeedBasedColor(pointA, pointB)
                )
            }
        }
    }

    polylineSegments.forEach { segmentsList ->
        segmentsList.forEach { segment ->
            Polyline(
                points = listOf(
                    LatLng(segment.locationA.lat, segment.locationA.long),
                    LatLng(segment.locationB.lat, segment.locationB.long)
                ),
                color = segment.color,
                jointType = JointType.BEVEL
            )
        }
    }
}