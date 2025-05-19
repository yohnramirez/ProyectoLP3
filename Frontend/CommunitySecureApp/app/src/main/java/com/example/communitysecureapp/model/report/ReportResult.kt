package com.example.communitysecureapp.model.report

data class ReportResult (
    val success: Boolean,
    val data: ReportResponse? = null,
    val errorMessage: String? = null
)