package com.example.communitysecureapp.utils.loader

data class LoaderStates<T> (
    val isLoading: Boolean = false,
    val result: T? = null,
    val errorMessage: String? = null
)