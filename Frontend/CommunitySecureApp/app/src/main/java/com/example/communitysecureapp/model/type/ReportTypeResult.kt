package com.example.communitysecureapp.model.type

data class ReportTypeResult (
    val success: Boolean,
    val data: List<ReportType>?,
    val errorMessage: String?
)