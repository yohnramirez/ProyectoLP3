package com.example.communitysecureapp.model.report

import org.osmdroid.util.GeoPoint

data class MapMarkerData (
    val id: Long,
    val userId: String,
    val geoPoint: GeoPoint,
    val status: String?,
    val title: String,
    val snippet: String,
    val typeName: String,
    val dateCreated: String
)