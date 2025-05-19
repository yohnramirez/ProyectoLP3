package com.example.communitysecureapp.state

import com.example.communitysecureapp.model.report.ReportResponse

sealed class MyReportsState {
    object Loading : MyReportsState()
    data class Success(val reports: List<ReportResponse>) : MyReportsState()
    data class Error(val message: String) : MyReportsState()
    object Empty : MyReportsState()
}