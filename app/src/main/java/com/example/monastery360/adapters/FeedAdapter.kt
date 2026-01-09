// FILE: com.example.monastery360/adapters/FeedAdapter.kt
package com.example.monastery360.adapters

import android.graphics.BitmapFactory
import android.util.Base64
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.monastery360.R
import com.example.monastery360.databinding.ItemPostBinding
import com.example.monastery360.model.Post

class FeedAdapter(
    private val onLikeClick: (String, Int) -> Unit,
    private val onCommentClick: (String, Int) -> Unit
) : ListAdapter<Post, FeedAdapter.PostViewHolder>(PostDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val binding = ItemPostBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return PostViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        holder.bind(getItem(position), position)
    }

    inner class PostViewHolder(
        private val binding: ItemPostBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(post: Post, position: Int) {

            binding.userName.text = post.userName
            binding.caption.text = post.caption
            binding.likesCount.text = "${post.likes.size} likes"

            binding.commentsCount.text = if (post.comments.isEmpty()) {
                "View all comments"
            } else "View all ${post.comments.size} comments"

            // NULL / EMPTY CHECK
            if (post.image.isNullOrEmpty()) {
                binding.postImage.setImageResource(R.drawable.ic_play_circle)
                return
            }

            // 🔥 Remove "data:image/jpeg;base64," prefix if exists
            val cleanBase64 = post.image.substringAfter(",")

            try {
                // 🔥 Decode Base64 safely
                val decodedBytes = Base64.decode(cleanBase64, Base64.DEFAULT)
                val bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)

                if (bitmap != null) {
                    binding.postImage.setImageBitmap(bitmap)
                } else {
                    binding.postImage.setImageResource(R.drawable.ic_play_circle)
                }

            } catch (e: Exception) {
                e.printStackTrace()
                binding.postImage.setImageResource(R.drawable.ic_play_circle)
            }

            // Like Button
            binding.likeBtn.setOnClickListener {
                onLikeClick(post.id, position)
            }

            // Comment Button
            binding.commentBtn.setOnClickListener {
                onCommentClick(post.id, position)
            }
        }
    }

    class PostDiffCallback : DiffUtil.ItemCallback<Post>() {
        override fun areItemsTheSame(oldItem: Post, newItem: Post): Boolean =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: Post, newItem: Post): Boolean =
            oldItem == newItem
    }
}
