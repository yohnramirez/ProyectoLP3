package com.example.communitysecureapp.state

import androidx.compose.runtime.saveable.mapSaver
import org.osmdroid.util.GeoPoint

data class FormState(
    val incidentLocation: GeoPoint?,
    val reportType: String,
    val description: String
) {
    companion object {
        val Saver = mapSaver(
            save = { formState ->
                mapOf(
                    "latitude" to formState.incidentLocation?.latitude,
                    "longitude" to formState.incidentLocation?.longitude,
                    "reportType" to formState.reportType,
                    "description" to formState.description
                )
            },
            restore = { map ->
                FormState(
                    incidentLocation = if (map["latitude"] != null && map["longitude"] != null) {
                        GeoPoint(map["latitude"] as Double, map["longitude"] as Double)
                    } else null,
                    reportType = map["reportType"] as String,
                    description = map["description"] as String
                )
            }
        )
    }
}