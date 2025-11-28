package com.example.monastery360.model

data class MonasteryWeather(
    val monasteryName: String,
    val temperature: Double,
    val feelsLike: Double,
    val humidity: Int,
    val pressure: Int,
    val description: String,
    val weatherIcon: String, // Full icon URL from WeatherAPI.com
    val windSpeed: Double,
    val cloudiness: Int,
    val tripSuitability: String, // EXCELLENT, GOOD, MODERATE, POOR, NOT_RECOMMENDED
    val tripAdviceText: String,
    val lastUpdated: Long
)