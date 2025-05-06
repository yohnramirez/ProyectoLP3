package com.example.communitysecureapp.repository

import com.example.communitysecureapp.model.document.TypeDocumentResult
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
}