package com.example.communitysecureapp.model.report

import java.sql.Timestamp

data class ReportHistoryResponse (
    val id: Long,
    val reportId: Long,
    val status: String,
    val timestamp: Timestamp
)