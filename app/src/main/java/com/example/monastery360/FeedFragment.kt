// FILE: com.example.monastery360/FeedActivity.kt
package com.example.monastery360

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import android.widget.EditText
import com.example.monastery360.adapters.FeedAdapter
import com.example.monastery360.databinding.ActivityFeedBinding
import com.example.monastery360.model.Post
import com.example.monastery360.model.Comment
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.auth.FirebaseAuth
import java.security.MessageDigest

class FeedActivity : AppCompatActivity() {
    private lateinit var binding: ActivityFeedBinding
    private lateinit var adapter: FeedAdapter
    private var posts = mutableListOf<Post>()
    private val firestore = FirebaseFirestore.getInstance()
    private val postsCollection = firestore.collection("posts")
    private val auth = FirebaseAuth.getInstance()

    private var currentUserId = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFeedBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Get user ID
        currentUserId = intent.getStringExtra("USER_ID") ?: ""

        if (currentUserId.isEmpty()) {
            Toast.makeText(this, "User ID not found", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        setupAdapter()

        // Anonymous login required for Firestore write rules
        if (auth.currentUser == null) {
            auth.signInAnonymously().addOnSuccessListener {
                loadAllPosts()
            }
        } else {
            loadAllPosts()
        }

        // Open create post page
        binding.fabCreatePost.setOnClickListener {
            val intent = Intent(this, CreatePostActivity::class.java)
            intent.putExtra("USER_ID", currentUserId)
            startActivity(intent)
        }
    }

    private fun setupAdapter() {
        adapter = FeedAdapter(
            onLikeClick = { postId, position ->
                if (position < posts.size) {
                    val post = posts[position]
                    post.isLiked = !post.isLiked

                    if (post.isLiked) post.likes.add(currentUserId)
                    else post.likes.remove(currentUserId)

                    updatePostInFirestore(post)
                    adapter.submitList(posts.toList())
                }
            },

            onCommentClick = { postId, position ->
                showCommentDialog(postId, position)
            }
        )

        binding.feedRecyclerView.adapter = adapter
        binding.feedRecyclerView.layoutManager = LinearLayoutManager(this)
    }

    private fun loadAllPosts() {
        binding.progressBar.visibility = android.view.View.VISIBLE

        postsCollection
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .limit(50)
            .addSnapshotListener { snapshot, error ->
                binding.progressBar.visibility = android.view.View.GONE

                if (error != null) {
                    Toast.makeText(this, "Error: ${error.message}", Toast.LENGTH_SHORT).show()
                    return@addSnapshotListener
                }

                posts.clear()

                if (snapshot != null && !snapshot.isEmpty) {
                    for (doc in snapshot.documents) {
                        val post = doc.toObject(Post::class.java)
                        if (post != null) {
                            post.id = doc.id
                            post.isLiked = post.likes.contains(currentUserId)

                            // ✅ BLOCKCHAIN: Verify image integrity
                            if (!post.imageHash.isEmpty()) {
                                val isVerified = verifyImageIntegrity(post.image, post.imageHash)
                                post.isTampered = !isVerified

                                if (!isVerified) {
                                    Toast.makeText(
                                        this@FeedActivity,
                                        "⚠️ Image tampered detected!",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }

                            posts.add(post)
                        }
                    }
                    adapter.submitList(posts.toList())
                }
            }
    }

    // ✅ BLOCKCHAIN: Verify image hash
    private fun verifyImageIntegrity(imageBase64: String, originalHash: String): Boolean {
        return try {
            val currentHash = calculateSHA256(imageBase64)
            currentHash == originalHash
        } catch (e: Exception) {
            false
        }
    }

    // ✅ BLOCKCHAIN: Calculate SHA-256
    private fun calculateSHA256(data: String): String {
        return try {
            val digest = MessageDigest.getInstance("SHA-256")
            val hashBytes = digest.digest(data.toByteArray(Charsets.UTF_8))
            hashBytes.joinToString("") { "%02x".format(it) }
        } catch (e: Exception) {
            ""
        }
    }

    private fun updatePostInFirestore(post: Post) {
        postsCollection.document(post.id).set(post)
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun showCommentDialog(postId: String, position: Int) {
        val editText = EditText(this).apply {
            hint = "Write a comment..."
            setPadding(16, 16, 16, 16)
        }

        AlertDialog.Builder(this)
            .setTitle("Add Comment")
            .setView(editText)
            .setPositiveButton("Post") { _, _ ->
                val text = editText.text.toString()
                if (text.isNotEmpty() && position < posts.size) {
                    val post = posts[position]
                    val comment = Comment(
                        userId = currentUserId,
                        userName = currentUserId,
                        userProfilePic = "",
                        text = text,
                        createdAt = System.currentTimeMillis()
                    )
                    post.comments.add(comment)
                    updatePostInFirestore(post)
                    adapter.submitList(posts.toList())
                    Toast.makeText(this, "Comment added!", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
}