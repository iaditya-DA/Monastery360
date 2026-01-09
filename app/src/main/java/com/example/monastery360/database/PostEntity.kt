package com.example.monastery360.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "posts")
data class PostEntity(
    @PrimaryKey val id: String,
    val userId: String,
    val userName: String,
    val userProfilePic: String,
    val image: String,
    val caption: String,
    val likes: String,
    val comments: String,
    val createdAt: Long,
    val isLiked: Boolean
)
