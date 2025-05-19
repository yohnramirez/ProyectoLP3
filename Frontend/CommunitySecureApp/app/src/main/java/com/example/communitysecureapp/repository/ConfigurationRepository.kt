package com.example.communitysecureapp.repository

import android.util.Log
import com.example.communitysecureapp.model.comment.CommentListResult
import com.example.communitysecureapp.model.comment.CommentRequest
import com.example.communitysecureapp.model.comment.CommentResult
import com.example.communitysecureapp.model.document.TypeDocumentResult
import com.example.communitysecureapp.model.gender.GenderResult
import com.example.communitysecureapp.model.report.ReportListResult
import com.example.communitysecureapp.model.report.ReportRequest
import com.example.communitysecureapp.model.report.ReportResult
import com.example.communitysecureapp.model.type.ReportTypeResult
import com.example.communitysecureapp.model.user.UserResult
import com.example.communitysecureapp.service.ApiService
import javax.inject.Inject

class ConfigurationRepository @Inject constructor(private val apiService: ApiService) {

    suspend fun getTypesDocument(): TypeDocumentResult {
        return try {
            var response = apiService.getTypesDocument()
            TypeDocumentResult(
                data = response,
                success = true
            )
        } catch (e: Exception) {
            TypeDocumentResult(
                data = null,
                success = false
            )
        }
    }

    suspend fun getGenders(): GenderResult {
        return try {
            var response = apiService.getGenders()
            GenderResult(
                success = true,
                data = response,
                errorMessage = null
            )
        } catch (e: Exception) {
            GenderResult(
                success = false,
                data = null,
                errorMessage = e.message
            )
        }
    }

    suspend fun getReportsByUserId(userId: String): ReportListResult {
        return try {
            var response = apiService.getReportsByUserId(userId)
            ReportListResult(
                success = true,
                data = response,
                errorMessage = null
            )
        } catch (e: Exception) {
            ReportListResult(
                success = false,
                data = null,
                errorMessage = e.message
            )
        }
    }

    suspend fun createReport(reportRequest: ReportRequest): ReportResult {
        return try {
            var response = apiService.createReport(reportRequest)
            Log.d("CreateReport", "Result: $response")
            ReportResult(
                success = true,
                data = response,
                errorMessage = null
            )
        } catch (e: Exception) {
            ReportResult(
                success = false,
                data = null,
                errorMessage = e.message
            )
        }
    }

    suspend fun getReportsByStatus(status: String): ReportListResult {
        return try {
            var response = apiService.getReportsByStatus(status)
            ReportListResult(
                success = true,
                data = response,
                errorMessage = null
            )

        } catch (e: Exception) {
            ReportListResult(
                success = false,
                data = null,
                errorMessage = e.message
            )
        }
    }

    suspend fun getUserById(userId: String): UserResult {
        return try {
            var response = apiService.getUserById(userId)
            Log.d("ConfigGetUserById", "Result: $response")
            UserResult(
                success = true,
                data = response,
                errorMessage = null
            )

        } catch (e: Exception) {
            UserResult(
                success = false,
                data = null,
                errorMessage = e.message
            )
        }
    }

    suspend fun getTypeReports(): ReportTypeResult {
        return try {
            var response = apiService.getReportTypes()
            Log.d("ConfigReportTypes", "Result: $response")
            ReportTypeResult(
                success = true,
                data = response,
                errorMessage = null
            )
        } catch (e: Exception) {
            ReportTypeResult(
                success = false,
                data = null,
                errorMessage = e.message
            )
        }
    }

    suspend fun updateReportStatus(idReport: Long, newStatus: String) : ReportResult {
        return try {
            var response = apiService.updateReportStatus(idReport, newStatus)
            ReportResult(
                success = true,
                data = response,
                errorMessage = null
            )
        } catch (e: Exception) {
            ReportResult(
                success = false,
                data = null,
                errorMessage = e.message
            )
        }
    }

    suspend fun createComment(comment: CommentRequest): CommentResult {
        return try {
            var response = apiService.createComment(comment)
            Log.d("ConfigCreateComment", "Result: $response")
            CommentResult(
                success = true,
                data = response,
                errorMessage = null
            )
        } catch (e: Exception) {
            CommentResult(
                success = false,
                data = null,
                errorMessage = e.message
            )
        }
    }

    suspend fun getCommentsByReportId(idReport: Long): CommentListResult {
        return try {
            var response = apiService.getCommentsByReportId(idReport)
            Log.d("ConfigGetCommentsByReportId", "Result: $response")
            CommentListResult(
                success = true,
                data = response,
                errorMessage = null
            )
        } catch (e: Exception) {
            CommentListResult(
                success = false,
                data = null,
                errorMessage = e.message
            )
        }
    }
}