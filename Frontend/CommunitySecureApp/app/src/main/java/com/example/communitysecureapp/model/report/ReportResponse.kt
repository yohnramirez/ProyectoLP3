package com.example.communitysecureapp.model.report

data class ReportResponse (
    val id: Long,
    val idUser: Long,
    val latitude: Double,
    val longitude: Double,
    val type: ReportType,
    val description: String,
    val status: String?,
    val state: Boolean,
    val imageUrl: String?
)

data class ReportType(
    val id: Long,
    val name: String,
    val description: String,
    val state: Boolean
)