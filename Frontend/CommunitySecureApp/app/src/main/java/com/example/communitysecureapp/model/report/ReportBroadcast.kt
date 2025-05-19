package com.example.communitysecureapp.model.report

import kotlinx.serialization.Serializable

@Serializable
data class ReportBroadcast (
    val id: Long,
    val userId: String,
    val latitude: Double,
    val longitude: Double,
    val type: String,
    val description: String,
    val status: String?,
    val dateCreated: String
)