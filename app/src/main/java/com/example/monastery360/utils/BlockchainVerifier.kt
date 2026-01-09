package com.example.monastery360.utils

import android.util.Base64
import android.util.Log
import java.security.MessageDigest

object BlockchainVerifier {
    private val TAG = "BlockchainVerifier"

    fun verifyImageIntegrity(imageBase64: String, originalHash: String): Boolean {
        return try {
            val currentHash = calculateSHA256(imageBase64)
            val isSafe = currentHash == originalHash

            Log.d(TAG, "Original Hash: $originalHash")
            Log.d(TAG, "Current Hash: $currentHash")
            Log.d(TAG, "Image Integrity: ${if (isSafe) "✅ SAFE" else "❌ TAMPERED"}")

            isSafe
        } catch (e: Exception) {
            Log.e(TAG, "Error verifying image", e)
            false
        }
    }

    private fun calculateSHA256(data: String): String {
        return try {
            val digest = MessageDigest.getInstance("SHA-256")
            val hashBytes = digest.digest(data.toByteArray(Charsets.UTF_8))
            hashBytes.joinToString("") { "%02x".format(it) }
        } catch (e: Exception) {
            Log.e(TAG, "Error calculating SHA256", e)
            ""
        }
    }
}
