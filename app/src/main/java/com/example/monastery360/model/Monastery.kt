package com.example.monastery360.model

data class Monastery(

    // 🔥 Old fields (NO CHANGE)
    val imageRes: Int = 0,   // now optional (because we will use URLs)
    val name: String,
    val distance: String = "",
    val rating: Float = 4.5f,
    val reviews: Int = 0,
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val location: String = "",
    val address: String = "",
    val description: String = "",
    val price: String = "",
    var isFavorite: Boolean = false,

    // 🔥 NEW FIELDS for JSON support
    val _id: String = "",
    val villageOrTown: String = "",
    val district: String = "",
    val state: String = "",
    val googleMapsLink: String = "",
    val altitude: String = "",
    val buddhistSect: String = "",
    val foundedYear: Int = 0,
    val history: String = "",
    val architecture: String = "",

    // 🔥 Images list from JSON
    val images: List<String> = emptyList(),

    // 🔥 Attractions
    val nearbyAttractions: List<String> = emptyList(),
)
