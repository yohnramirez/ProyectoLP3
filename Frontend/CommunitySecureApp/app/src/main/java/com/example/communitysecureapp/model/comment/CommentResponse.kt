package com.example.communitysecureapp.model.comment

data class CommentResponse (
    val id: Long,
    val reportId: Long,
    val userId: String,
    val comment: String,
    val createdAt: String
)