package com.example.monastery360.model

data class Monastery(
    val imageRes: Int,
    val name: String,
    val distance: String,
    val rating: Float = 4.5f,
    val reviews: Int = 455,
    val location: String = "Gangtok, Sikkim",
    val address: String = "Gangtok, East Sikkim",
    val description: String = "One of the largest and most significant monasteries in Sikkim. Built in the traditional Buddhist architectural style with stunning mountain views.",
    val price: String = "₹500/person",

    // ❤️ Add this line
    var isFavorite: Boolean = false
)
