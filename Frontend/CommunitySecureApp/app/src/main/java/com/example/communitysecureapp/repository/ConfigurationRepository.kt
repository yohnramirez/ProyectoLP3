package com.example.communitysecureapp.repository

import com.example.communitysecureapp.model.document.TypeDocument
import com.example.communitysecureapp.model.document.TypeDocumentResult
import com.example.communitysecureapp.service.ApiService
import javax.inject.Inject

class ConfigurationRepository @Inject constructor(private val apiService: ApiService) {

    suspend fun getTypesDocument(): TypeDocumentResult {
        try {
            var response = apiService.getTypesDocument()
        } catch (e: Exception) {

        }
    }
}