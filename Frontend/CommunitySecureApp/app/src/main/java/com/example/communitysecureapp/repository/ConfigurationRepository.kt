package com.example.communitysecureapp.repository

import android.util.Log
import com.example.communitysecureapp.model.document.TypeDocumentResult
import com.example.communitysecureapp.model.gender.GenderResult
import com.example.communitysecureapp.model.type.ReportTypeResult
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
}