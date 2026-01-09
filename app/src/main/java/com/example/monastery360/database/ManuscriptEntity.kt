package com.example.monastery360.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "manuscripts")
data class ManuscriptEntity(
    @PrimaryKey
    val id: String,
    val filename: String,
    val filepath: String,
    val timestamp: Long,
    val detectedText: String,
    val translatedText: String,
    val targetLanguage: String
)