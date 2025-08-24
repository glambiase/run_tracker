@file:OptIn(ExperimentalCoroutinesApi::class)

package com.glambiase.run.domain

import com.glambiase.core.domain.Timer
import com.glambiase.core.domain.location.LocationWithAltitudeAndTimestamp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.combineTransform
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlin.time.Duration

class RunningTracker(
    private val locationObserver: LocationObserver,
    applicationScope: CoroutineScope
) {
    private val _runData = MutableStateFlow(RunData())
    val runData = _runData.asStateFlow()

    private val _elapsedTime = MutableStateFlow(Duration.ZERO)
    val elapsedTime = _elapsedTime.asStateFlow()

    private val _isTracking = MutableStateFlow(false)
    val isTracking = _isTracking.asStateFlow()

    private val isObservingLocation = MutableStateFlow(false)

    val currentLocation = isObservingLocation
        .flatMapLatest { isObservingLocation ->
            if (isObservingLocation) locationObserver.observeLocation(OBSERVE_LOCATION_INTERVAL_MILLIS)
            else emptyFlow()
        }
        .stateIn(scope = applicationScope, started = SharingStarted.Lazily, initialValue = null)

    init {
        isTracking
            .onEach { isTracking ->
                if (isTracking) {
                    _runData.update {
                        val locations = it.locations
                        val needsNew = locations.isEmpty() || locations.last().isNotEmpty()
                        if (needsNew) it.copy(locations = locations + listOf(emptyList())) else it
                    }
                }
            }
            .flatMapLatest { isTracking ->
                if (isTracking) Timer.elapsedTimeFlow()
                else emptyFlow()
            }
            .onEach {
                _elapsedTime.value += it
            }
            .launchIn(applicationScope)

        currentLocation
            .filterNotNull()
            .combineTransform(isTracking) { locationWithAltitude, isTracking ->
                if (isTracking) emit(locationWithAltitude)
            }
            .combine(elapsedTime) { locationWithAltitude, elapsedTime ->
                LocationWithAltitudeAndTimestamp(
                    locationWithAltitude = locationWithAltitude,
                    timestamp = elapsedTime
                )
            }
            .onEach { locationWithAltitudeAndTimestamp ->
                // Append the new timestamped location to the active segment (the last inner list).
                // A fresh empty segment is created when tracking turns ON, so the first post-resume point starts a new polyline (no "teleport" across pauses).
                _runData.update {
                    val currentLocationsList = it.locations

                    // Fallback (shouldn't happen)
                    // When a point arrives, there must be at least one (possibly empty) segment. If this ever fails, we recover gracefully and start the first segment with this point.
                    if (currentLocationsList.isEmpty()) return@update it.copy(locations = listOf(listOf(locationWithAltitudeAndTimestamp)))

                    val lastLocations = currentLocationsList.last() + locationWithAltitudeAndTimestamp
                    val newLocationsList = currentLocationsList.replaceLastList(lastLocations)
                    val totalDistanceMeters = LocationDataCalculator.getTotalDistanceMeters(newLocationsList)
                    it.copy(
                        distanceMeters = totalDistanceMeters,
                        locations = newLocationsList
                    )
                }
            }
            .launchIn(applicationScope)
    }

    fun startObservingLocation() {
        isObservingLocation.value = true
    }

    fun stopObservingLocation() {
        isObservingLocation.value = false
    }

    fun setIsTracking(isTracking: Boolean) {
        _isTracking.value = isTracking
    }

    private fun <T> List<List<T>>.replaceLastList(list: List<T>) =
        if (isEmpty()) listOf(list)
        else dropLast(1) + listOf(list)

    companion object {
        private const val OBSERVE_LOCATION_INTERVAL_MILLIS = 1000L
    }
}