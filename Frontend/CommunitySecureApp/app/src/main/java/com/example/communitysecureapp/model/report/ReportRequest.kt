package com.example.communitysecureapp.model.report

data class ReportRequest (
    val userId: String,
    val latitude: Double,
    val longitude: Double,
    val typeId: Long,
    val description: String,
    val imageUrl: String?
)