package com.example.monastery360.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * Data class for Digital Archive/Manuscript
 * Represents a manuscript document with images and metadata
 */
@Parcelize
data class DigitalArchive(
    val _id: String,
    val monasteryId: String,
    val title: String,
    val type: String,
    val fileUrl: String,
    val description: String,
    val images: List<String>
) : Parcelable