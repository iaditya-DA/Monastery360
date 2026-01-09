package com.example.monastery360.model

data class UploadedManuscript(
    val id: String,
    val filename: String,
    val filepath: String,
    val timestamp: Long,
    val detectedText: String,
    val translatedText: String,
    val targetLanguage: String
) {
    // Helper: formatted time for UI
    fun getFormattedTime(): String {
        val date = java.util.Date(timestamp)
        val format = java.text.SimpleDateFormat("dd MMM yyyy, hh:mm a", java.util.Locale.getDefault())
        return format.format(date)
    }
}
