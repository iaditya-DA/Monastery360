package com.example.monastery360.model

data class Comment(
    val userId: String = "user456",
    val userName: String = "",
    val userProfilePic: String = "",
    val text: String = "",
    val createdAt: Long = System.currentTimeMillis()
)