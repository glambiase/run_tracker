@file:OptIn(MapsComposeExperimentalApi::class)

package com.glambiase.run.presentation.active_run.maps

import android.graphics.Bitmap
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import com.glambiase.core.domain.location.Location
import com.glambiase.core.domain.location.LocationWithAltitudeAndTimestamp
import com.glambiase.core.presentation.designsystem.RunIcon
import com.glambiase.run.presentation.R
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapEffect
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.MapsComposeExperimentalApi
import com.google.maps.android.compose.MarkerComposable
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberMarkerState
import com.google.maps.android.ktx.awaitMapLoad
import com.google.maps.android.ktx.awaitSnapshot
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

@Composable
fun RunTrackerMap(
    isRunFinished: Boolean,
    currentLocation: Location?,
    locations: List<List<LocationWithAltitudeAndTimestamp>>,
    onSnapshot: (Bitmap) -> Unit,
    modifier: Modifier
) {
    val context = LocalContext.current
    val mapStyle = remember {
        MapStyleOptions.loadRawResourceStyle(context, R.raw.map_style)
    }
    val cameraPositionState = rememberCameraPositionState()
    val markerState = rememberMarkerState()

    val scope = rememberCoroutineScope()
    val density = LocalDensity.current
    val boundsPaddingPx = with(density) { 24.dp.toPx().toInt() }

    val markerPositionLat by animateFloatAsState(
        targetValue = currentLocation?.lat?.toFloat() ?: 0f,
        animationSpec = tween(durationMillis = 500)
    )
    val markerPositionLong by animateFloatAsState(
        targetValue = currentLocation?.long?.toFloat() ?: 0f,
        animationSpec = tween(durationMillis = 500)
    )
    val markerPosition = remember(markerPositionLat, markerPositionLong) {
        LatLng(markerPositionLat.toDouble(), markerPositionLong.toDouble())
    }

    LaunchedEffect(markerPosition, isRunFinished) {
        if (!isRunFinished) markerState.position = markerPosition
    }

    LaunchedEffect(currentLocation, isRunFinished) {
        if (currentLocation != null && !isRunFinished) {
            val latLng = LatLng(currentLocation.lat, currentLocation.long)
            cameraPositionState.animate(
                update = CameraUpdateFactory.newLatLngZoom(latLng, 16f)
            )
        }
    }

    var getScreenshot by remember { mutableStateOf(false) }
    var snapshotJob by remember { mutableStateOf<Job?>(null) }

    // Cancel any in-flight snapshot when the composable leaves
    DisposableEffect(Unit) {
        onDispose {
            snapshotJob?.cancel()
            snapshotJob = null
        }
    }

    GoogleMap(
        cameraPositionState = cameraPositionState,
        properties = MapProperties(mapStyleOptions = mapStyle),
        uiSettings = MapUiSettings(zoomControlsEnabled = false),
        modifier = if (isRunFinished) {
            // Render a smaller, invisible map just for the screenshot
            modifier
                .width(300.dp)
                .aspectRatio(16 / 9f)
                .alpha(0f)
                .onSizeChanged {
                    if (it.width >= 300) getScreenshot = true
                }
        } else modifier
    ) {
        RunTrackerPolyline(locations = locations)

        // Use moveCamera (instant, invisible), then wait for idle + tiles rendered, then snapshot
        MapEffect(locations, isRunFinished, getScreenshot) { map ->
            if (isRunFinished && getScreenshot && snapshotJob == null) {
                getScreenshot = false

                val locationsList = locations.flatten()
                if (locationsList.isEmpty()) return@MapEffect

                // One-shot: after the camera settles, wait for tiles to render, then take snapshot
                var handled = false
                map.setOnCameraIdleListener(object : GoogleMap.OnCameraIdleListener {
                    override fun onCameraIdle() {
                        if (handled) return

                        handled = true
                        map.setOnCameraIdleListener(null) // Remove listener right away. Now it is safe to take the snapshot

                        snapshotJob?.cancel()
                        snapshotJob = scope.launch {
                            map.awaitMapLoad()
                            map.awaitSnapshot()?.let(onSnapshot)
                            snapshotJob = null
                        }
                    }
                })

                if (locationsList.size >= 2) {
                    val bounds = LatLngBounds.builder().apply {
                        locationsList.forEach {
                            include(
                                LatLng(
                                    it.locationWithAltitude.location.lat,
                                    it.locationWithAltitude.location.long
                                )
                            )
                        }
                    }.build()
                    map.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, boundsPaddingPx))
                } else {
                    val only = locationsList.first()
                    val target = LatLng(
                        only.locationWithAltitude.location.lat,
                        only.locationWithAltitude.location.long
                    )
                    map.moveCamera(CameraUpdateFactory.newLatLngZoom(target, 16f))
                }
            }
        }

        if (currentLocation != null && !isRunFinished) {
            MarkerComposable(currentLocation, state = markerState) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = RunIcon,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier
                            .size(24.dp)
                    )
                }
            }
        }
    }
}