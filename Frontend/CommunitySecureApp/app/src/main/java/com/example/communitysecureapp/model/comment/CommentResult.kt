package com.example.communitysecureapp.model.comment

data class CommentResult (
    val success: Boolean,
    val data: CommentResponse?,
    val errorMessage: String? = null
)