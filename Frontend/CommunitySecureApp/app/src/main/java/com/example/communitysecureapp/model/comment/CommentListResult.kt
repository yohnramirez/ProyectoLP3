package com.example.communitysecureapp.model.comment

data class CommentListResult (
    val success: Boolean,
    val data: List<CommentResponse>?,
    val errorMessage: String? = null
)