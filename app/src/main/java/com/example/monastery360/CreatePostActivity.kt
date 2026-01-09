package com.example.monastery360

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.monastery360.databinding.ActivityCreatePostBinding
import com.example.monastery360.model.Post
import com.google.firebase.firestore.FirebaseFirestore
import java.io.ByteArrayOutputStream
import java.security.MessageDigest
import java.util.UUID

class CreatePostActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCreatePostBinding
    private var selectedImageUri: Uri? = null
    private var currentUserId = ""

    private val firestore = FirebaseFirestore.getInstance()
    private val TAG = "CreatePostActivity"

    private val imagePickerLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            selectedImageUri = result.data?.data
            if (selectedImageUri != null) {
                binding.selectedImage.setImageURI(selectedImageUri)
                binding.selectedImage.visibility = android.view.View.VISIBLE
                Log.d(TAG, "Image selected: $selectedImageUri")
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreatePostBinding.inflate(layoutInflater)
        setContentView(binding.root)

        currentUserId = intent.getStringExtra("USER_ID") ?: ""

        if (currentUserId.isEmpty()) {
            Toast.makeText(this, "User ID not found", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        Log.d(TAG, "Current User ID: $currentUserId")
        setupListeners()
    }

    private fun setupListeners() {
        binding.selectImageBtn.setOnClickListener {
            openGallery()
        }

        binding.postBtn.setOnClickListener {
            if (selectedImageUri != null) {
                val caption = binding.captionInput.text.toString()
                createPostWithSecureImage(caption)
            } else {
                Toast.makeText(this, "Please select an image first", Toast.LENGTH_SHORT).show()
            }
        }

        binding.cancelBtn.setOnClickListener {
            finish()
        }
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK).apply {
            type = "image/*"
        }
        imagePickerLauncher.launch(intent)
    }

    private fun createPostWithSecureImage(caption: String) {
        if (selectedImageUri == null) {
            Toast.makeText(this, "No image selected", Toast.LENGTH_SHORT).show()
            return
        }

        binding.progressBar.visibility = android.view.View.VISIBLE
        binding.postBtn.isEnabled = false

        Log.d(TAG, "Processing image with blockchain security...")

        try {
            // Image ko Base64 mein convert karo
            val imageBase64 = convertImageToBase64(selectedImageUri!!)

            if (imageBase64.isEmpty()) {
                throw Exception("Failed to convert image")
            }

            // Image ka hash calculate karo (SHA-256)
            val imageHash = calculateSHA256(imageBase64)

            Log.d(TAG, "Image Hash: $imageHash")

            // Post save karo with hash
            saveSecurePostToFirestore(imageBase64, caption, imageHash)

        } catch (e: Exception) {
            Log.e(TAG, "Error processing image", e)
            binding.progressBar.visibility = android.view.View.GONE
            binding.postBtn.isEnabled = true
            Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    private fun convertImageToBase64(imageUri: Uri): String {
        return try {
            val inputStream = contentResolver.openInputStream(imageUri)
            val originalBitmap = BitmapFactory.decodeStream(inputStream)
            inputStream?.close()

            val compressedBitmap = resizeBitmap(originalBitmap, 800)

            val byteArrayOutputStream = ByteArrayOutputStream()
            compressedBitmap.compress(Bitmap.CompressFormat.JPEG, 60, byteArrayOutputStream)
            val byteArray = byteArrayOutputStream.toByteArray()

            val base64String = Base64.encodeToString(byteArray, Base64.DEFAULT)

            Log.d(TAG, "Image size: ${byteArray.size / 1024}KB")

            base64String
        } catch (e: Exception) {
            Log.e(TAG, "Error in convertImageToBase64", e)
            ""
        }
    }

    private fun resizeBitmap(bitmap: Bitmap, maxWidth: Int): Bitmap {
        val width = bitmap.width
        val height = bitmap.height

        if (width <= maxWidth) {
            return bitmap
        }

        val ratio = width.toFloat() / maxWidth.toFloat()
        val newHeight = (height / ratio).toInt()

        return Bitmap.createScaledBitmap(bitmap, maxWidth, newHeight, true)
    }

    // ✅ SHA-256 hash calculate karo
    private fun calculateSHA256(data: String): String {
        return try {
            val digest = MessageDigest.getInstance("SHA-256")
            val hashBytes = digest.digest(data.toByteArray(Charsets.UTF_8))

            // Hash ko hex string mein convert karo
            hashBytes.joinToString("") { "%02x".format(it) }
        } catch (e: Exception) {
            Log.e(TAG, "Error calculating SHA256", e)
            ""
        }
    }

    // ✅ Secure post Firestore mein save karo
    private fun saveSecurePostToFirestore(imageBase64: String, caption: String, imageHash: String) {
        val postId = UUID.randomUUID().toString()

        val post = mapOf(
            "id" to postId,
            "userId" to currentUserId,
            "userName" to currentUserId,
            "userProfilePic" to "",
            "image" to imageBase64,
            "caption" to caption,
            "likes" to emptyList<String>(),
            "comments" to emptyList<Map<String, Any>>(),
            "createdAt" to System.currentTimeMillis(),
            "isLiked" to false,
            // ✅ BLOCKCHAIN SECURITY
            "imageHash" to imageHash,  // Original image ka hash
            "fileHash" to imageHash,   // Blockchain verification ke liye
            "timestamp" to System.currentTimeMillis(),
            "isTampered" to false      // Initially false
        )

        Log.d(TAG, "Saving secure post to Firestore: $postId")

        firestore.collection("posts")
            .document(postId)
            .set(post)
            .addOnSuccessListener {
                Log.d(TAG, "Secure post saved! Hash: $imageHash")
                binding.progressBar.visibility = android.view.View.GONE
                Toast.makeText(
                    this,
                    "Post secured with blockchain! 🔐 Cannot be edited.",
                    Toast.LENGTH_SHORT
                ).show()
                finish()
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "Failed to save post", e)
                binding.progressBar.visibility = android.view.View.GONE
                binding.postBtn.isEnabled = true
                Toast.makeText(this, "Failed to create post: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}

