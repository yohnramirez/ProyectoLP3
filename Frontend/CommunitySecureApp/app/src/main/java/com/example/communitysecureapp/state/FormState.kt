package com.example.communitysecureapp.state

import androidx.compose.runtime.saveable.mapSaver
import org.osmdroid.util.GeoPoint

data class FormState(
    val incidentLocation: GeoPoint?,
    val typeId: Long?,
    val description: String,
    val imageUrl: String?
) {
    companion object {
        val Saver = mapSaver(
            save = { formState ->
                mapOf(
                    "latitude" to formState.incidentLocation?.latitude,
                    "longitude" to formState.incidentLocation?.longitude,
                    "typeId" to formState.typeId,
                    "description" to formState.description,
                    "imageUrl" to formState.imageUrl
                )
            },
            restore = { map ->
                FormState(
                    incidentLocation = if (map["latitude"] != null && map["longitude"] != null) {
                        GeoPoint(map["latitude"] as Double, map["longitude"] as Double)
                    } else null,
                    typeId = map["typeId"] as Long?,
                    description = map["description"] as String,
                    imageUrl = map["imageUrl"] as String?
                )
            }
        )
    }
}