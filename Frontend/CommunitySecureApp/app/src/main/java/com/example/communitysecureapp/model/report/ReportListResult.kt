package com.example.communitysecureapp.model.report

data class ReportListResult (
    val success: Boolean,
    val data: List<ReportResponse>? = null,
    val errorMessage: String? = null
)