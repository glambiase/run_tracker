package com.glambiase.run.domain

import com.glambiase.core.domain.location.LocationWithAltitude
import kotlinx.coroutines.flow.Flow

interface LocationObserver {
    fun observeLocation(intervalMillis: Long): Flow<LocationWithAltitude>
}