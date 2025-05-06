package com.example.communitysecureapp.model.report

data class ReportRequest (
    val idUser: Long,
    val latitude: Double,
    val longitude: Double,
    val idTypeReport: Long,
    val description: String,
    val imageUrl: String?
)