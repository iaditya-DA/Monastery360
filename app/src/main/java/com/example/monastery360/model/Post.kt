// FILE: com.example.monastery360/model/Post.kt
package com.example.monastery360.model

import java.util.UUID

data class Post(
    var id: String = UUID.randomUUID().toString(),
    val userId: String = "user1",
    val userName: String = "",
    val userProfilePic: String = "",
    val image: String = "",
    val caption: String = "",
    var likes: MutableList<String> = mutableListOf(),
    var comments: MutableList<Comment> = mutableListOf(),
    val createdAt: Long = System.currentTimeMillis(),
    var isLiked: Boolean = false,
    val imageHash: String = "",  // ✅ ADD KAREIN
    var isTampered: Boolean = false
)