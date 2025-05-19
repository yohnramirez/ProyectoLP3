package com.example.communitysecureapp.model.comment

data class CommentRequest (
    val reportId: Long,
    val userId: String,
    val comment: String,
)