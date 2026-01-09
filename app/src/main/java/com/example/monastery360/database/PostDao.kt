package com.example.monastery360.database

import androidx.room.*
import com.example.monastery360.database.PostEntity

@Dao
interface PostDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPost(post: PostEntity)

    @Query("SELECT * FROM posts ORDER BY createdAt DESC LIMIT :limit OFFSET :offset")
    suspend fun getPosts(limit: Int, offset: Int): List<PostEntity>

    @Update
    suspend fun updatePost(post: PostEntity)

    @Delete
    suspend fun deletePost(post: PostEntity)

    @Query("DELETE FROM posts")
    suspend fun deleteAllPosts()
}