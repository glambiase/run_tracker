package com.glambiase.run.location

import android.location.Location
import com.glambiase.core.domain.location.LocationWithAltitude


fun Location.toLocationWithAltitude() =
    LocationWithAltitude(
        location = com.glambiase.core.domain.location.Location(
            lat = latitude,
            long = longitude
        ),
        altitude = altitude
    )