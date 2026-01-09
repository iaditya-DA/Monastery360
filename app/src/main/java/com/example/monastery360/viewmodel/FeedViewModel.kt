// FILE: com.example.monastery360/viewmodel/FeedViewModel.kt
package com.example.monastery360.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.monastery360.model.Comment
import com.example.monastery360.model.Post

class FeedViewModel : ViewModel() {
    private val _feedPosts = MutableLiveData<List<Post>>(emptyList())
    val feedPosts: LiveData<List<Post>> = _feedPosts

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String?>(null)
    val error: LiveData<String?> = _error

    fun loadFeed(posts: List<Post>) {
        _feedPosts.value = posts
        _isLoading.value = false
    }

    fun likePost(postId: String, position: Int) {
        try {
            val currentList = _feedPosts.value ?: emptyList()
            if (position < currentList.size) {
                val updatedList = currentList.toMutableList()
                val post = updatedList[position]
                updatedList[position] = post.copy(
                    isLiked = !post.isLiked,
                    likes = if (!post.isLiked) {
                        (post.likes + "currentUser").toMutableList()
                    } else {
                        (post.likes - "currentUser").toMutableList()
                    }
                )
                _feedPosts.value = updatedList
            }
        } catch (e: Exception) {
            _error.value = e.message
        }
    }

    fun addComment(postId: String, text: String, position: Int) {
        try {
            val comment = Comment(
                userName = "Current User",
                userProfilePic = "https://via.placeholder.com/40",
                text = text
            )
            val currentList = _feedPosts.value ?: emptyList()
            if (position < currentList.size) {
                val updatedList = currentList.toMutableList()
                updatedList[position] = updatedList[position].copy(
                    comments = (updatedList[position].comments + comment).toMutableList()
                )
                _feedPosts.value = updatedList
            }
        } catch (e: Exception) {
            _error.value = e.message
        }
    }

    fun createPost(caption: String, imageUrl: String) {
        try {
            val newPost = Post(
                userName = "Your Name",
                userProfilePic = "https://via.placeholder.com/40",
                image = imageUrl,
                caption = caption
            )
            val currentList = _feedPosts.value ?: emptyList()
            _feedPosts.value = listOf(newPost) + currentList
        } catch (e: Exception) {
            _error.value = e.message
        }
    }
}