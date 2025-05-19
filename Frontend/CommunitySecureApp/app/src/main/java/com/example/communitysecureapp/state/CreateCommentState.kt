package com.example.communitysecureapp.state

sealed class CreateCommentState {
    object Idle : CreateCommentState()
    object Loading : CreateCommentState()
    data class Success(val message: String) : CreateCommentState()
    data class Error(val errorMessage: String) : CreateCommentState()
}