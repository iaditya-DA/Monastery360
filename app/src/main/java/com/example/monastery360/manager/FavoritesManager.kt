package com.example.monastery360.manager

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson

object FavoritesManager {
    private lateinit var sharedPreferences: SharedPreferences
    private val gson = Gson()
    private const val PREF_NAME = "monastery_favorites"
    private const val FAV_KEY = "favorites"

    fun init(context: Context) {
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    }

    fun addFavorite(monasteryName: String) {
        val favorites = getFavorites().toMutableSet()
        favorites.add(monasteryName)
        saveFavorites(favorites)
    }

    fun removeFavorite(monasteryName: String) {
        val favorites = getFavorites().toMutableSet()
        favorites.remove(monasteryName)
        saveFavorites(favorites)
    }

    fun isFavorite(monasteryName: String): Boolean {
        return getFavorites().contains(monasteryName)
    }

    fun getFavorites(): Set<String> {
        val json = sharedPreferences.getString(FAV_KEY, "[]")
        return try {
            gson.fromJson(json, Array<String>::class.java).toSet()
        } catch (e: Exception) {
            emptySet()
        }
    }

    private fun saveFavorites(favorites: Set<String>) {
        val json = gson.toJson(favorites.toList())
        sharedPreferences.edit().putString(FAV_KEY, json).apply()
    }
}