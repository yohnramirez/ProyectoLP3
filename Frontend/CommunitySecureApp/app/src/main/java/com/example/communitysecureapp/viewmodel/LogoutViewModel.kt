package com.example.communitysecureapp.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import androidx.core.content.edit

@HiltViewModel
class LogoutViewModel @Inject constructor(private val appContext: Application): ViewModel() {

    fun logOut() {
        val prefs = appContext.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)
        prefs.edit() { clear() }
    }

}