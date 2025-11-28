package com.example.monastery360

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.widget.Toast

class ProfileActivity : AppCompatActivity() {

    private lateinit var backButton: ImageView
    private lateinit var settingsIcon: ImageView
    private lateinit var profilePicture: ImageView
    private lateinit var profileName: TextView
    private lateinit var profileEmail: TextView
    private lateinit var editProfileButton: Button
    private lateinit var menuRecycler: RecyclerView
    private lateinit var profileMenuAdapter: ProfileMenuAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        backButton = findViewById(R.id.backButtonProfile)
        settingsIcon = findViewById(R.id.settingsIcon)
        profilePicture = findViewById(R.id.profilePicture)
        profileName = findViewById(R.id.profileName)
        profileEmail = findViewById(R.id.profileEmail)
        editProfileButton = findViewById(R.id.editProfileButton)
        menuRecycler = findViewById(R.id.profileMenuRecycler)

        // Set user data (dummy for now)
        profileName.text = "Sabrina Aryan"
        profileEmail.text = "SabrinaAry208@gmail.com"

        // Back button
        backButton.setOnClickListener {
            finish()
        }

        // Edit Profile button
        editProfileButton.setOnClickListener {
            startActivity(Intent(this, EditProfileActivity::class.java))
        }

        // Settings icon
        settingsIcon.setOnClickListener {
            Toast.makeText(this, "Settings", Toast.LENGTH_SHORT).show()
        }

        // Setup menu items
        setupMenuItems()
    }

    private fun setupMenuItems() {
        val menuItems = listOf(
            ProfileMenuItem("Favourites", R.drawable.favorite),
            ProfileMenuItem("Languages", R.drawable.ic_language),
            ProfileMenuItem("Location", R.drawable.ic_location_pinic_location_pin),
            ProfileMenuItem("Clear Cache", R.drawable.ic_clear),
            ProfileMenuItem("Clear History", R.drawable.ic_history),
            ProfileMenuItem("Log Out", R.drawable.ic_logout)
        )

        profileMenuAdapter = ProfileMenuAdapter(menuItems) { item ->
            handleMenuClick(item.title)
        }

        menuRecycler.layoutManager = LinearLayoutManager(this)
        menuRecycler.adapter = profileMenuAdapter
    }

    private fun handleMenuClick(itemTitle: String) {
        when (itemTitle) {
            "Favourites" -> {
                startActivity(Intent(this, FavoritesActivity::class.java))
            }
            "Languages" -> {
                startActivity(Intent(this, LanguageSelectionActivity::class.java))
            }
            "Location" -> {
                startActivity(Intent(this, LocationMapActivity::class.java))
            }
            "Clear Cache" -> {
                showConfirmDialog("Clear Cache", "Are you sure?") {
                    Toast.makeText(this, "Cache cleared", Toast.LENGTH_SHORT).show()
                }
            }
            "Clear History" -> {
                showConfirmDialog("Clear History", "Are you sure?") {
                    Toast.makeText(this, "History cleared", Toast.LENGTH_SHORT).show()
                }
            }
            "Log Out" -> {
                showConfirmDialog("Log Out", "Are you sure you want to logout?") {
                    logout()
                }
            }
        }
    }

    private fun showConfirmDialog(title: String, message: String, onConfirm: () -> Unit) {
        AlertDialog.Builder(this)
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton("Yes") { _, _ -> onConfirm() }
            .setNegativeButton("No", null)
            .show()
    }

    private fun logout() {
        // Clear preferences
        val prefs = getSharedPreferences("AppSettings", MODE_PRIVATE)
        prefs.edit().clear().apply()

        // Go to login
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}



