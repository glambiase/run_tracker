package com.glambiase.run.presentation.permissions

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import androidx.core.content.ContextCompat

/**
 * shouldShowLocationPermissionRationale() and shouldShowNotificationPermissionRationale() are
 * wrappers around Android's shouldShowRequestPermissionRationale().
 *
 * This method returns:
 * - true  → The user has previously denied the permission request,
 *           but did NOT select "Don't ask again".
 *           In this case, you should show your own rationale (e.g. a dialog explaining why
 *           the permission is needed) before requesting it again.
 *
 * - false → One of these cases:
 *           • The permission has never been requested before (first time request),
 *           • The permission is already granted,
 *           • The user denied and selected "Don't ask again" (system dialog won't show again),
 *           • The permission cannot be granted due to system policy restrictions.
 *
 * In short:
 * This method is used to decide whether to show an explanation UI before re-requesting
 * a permission that was previously denied.
 */
fun ComponentActivity.shouldShowLocationPermissionRationale() =
    shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)

fun ComponentActivity.shouldShowNotificationPermissionRationale() =
    Build.VERSION.SDK_INT >= 33 && shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS)

fun ActivityResultLauncher<Array<String>>.requestRunTrackerPermissions(context: Context) {
    val hasLocationPermission = context.hasLocationPermission()
    val hasNotificationPermission = context.hasNotificationPermission()

    val locationPermissions = arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION)
    val notificationPermissions = if (Build.VERSION.SDK_INT >= 33) arrayOf(Manifest.permission.POST_NOTIFICATIONS) else emptyArray()

    when {
        !hasLocationPermission && !hasNotificationPermission -> launch(locationPermissions + notificationPermissions)
        !hasLocationPermission -> launch(locationPermissions)
        !hasNotificationPermission -> launch(notificationPermissions)
    }
}

fun Context.hasLocationPermission() = hasPermission(Manifest.permission.ACCESS_FINE_LOCATION)

fun Context.hasNotificationPermission() = if (Build.VERSION.SDK_INT >= 33) hasPermission(Manifest.permission.POST_NOTIFICATIONS) else true

private fun Context.hasPermission(permission: String) =
    ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED