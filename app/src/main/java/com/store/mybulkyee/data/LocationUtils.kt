package com.store.mybulkyee.data

import android.location.Location

object LocationUtils {
    fun calculateDistance(
        userLat: Double,
        userLng: Double,
        targetLat: Double,
        targetLng: Double
    ): Float {
        val userLocation = Location("").apply {
            latitude = userLat
            longitude = userLng
        }
        val targetLocation = Location("").apply {
            latitude = targetLat
            longitude = targetLng
        }
        return userLocation.distanceTo(targetLocation) // Distance in meters
    }
}
