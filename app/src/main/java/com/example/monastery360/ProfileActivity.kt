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
import com.example.monastery360.utils.LocaleHelper

class ProfileActivity : BaseActivity() {

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
            ProfileMenuItem(getString(R.string.menu_favourites), R.drawable.favorite),
            ProfileMenuItem(getString(R.string.menu_languages), R.drawable.ic_language),
            ProfileMenuItem(getString(R.string.menu_location), R.drawable.ic_location_pinic_location_pin),
            ProfileMenuItem(getString(R.string.menu_clear_cache), R.drawable.ic_clear),
            ProfileMenuItem(getString(R.string.menu_clear_history), R.drawable.ic_history),
            ProfileMenuItem(getString(R.string.menu_logout), R.drawable.ic_logout)
        )

        profileMenuAdapter = ProfileMenuAdapter(menuItems) { item ->
            handleMenuClick(item.title)
        }

        menuRecycler.layoutManager = LinearLayoutManager(this)
        menuRecycler.adapter = profileMenuAdapter
    }

    private fun handleMenuClick(itemTitle: String) {
        when (itemTitle) {
            getString(R.string.menu_favourites) -> {
                startActivity(Intent(this, FavoritesActivity::class.java))
            }
            getString(R.string.menu_languages) -> {
                showLanguageSelectionDialog()  // ✅ Change here
            }
            getString(R.string.menu_location) -> {
                startActivity(Intent(this, LocationMapActivity::class.java))
            }
            getString(R.string.menu_clear_cache) -> {
                showConfirmDialog(
                    getString(R.string.dialog_clear_cache),
                    getString(R.string.dialog_are_you_sure)
                ) {
                    Toast.makeText(this, getString(R.string.dialog_cache_cleared), Toast.LENGTH_SHORT).show()
                }
            }
            getString(R.string.menu_clear_history) -> {
                showConfirmDialog(
                    getString(R.string.dialog_clear_history),
                    getString(R.string.dialog_are_you_sure)
                ) {
                    Toast.makeText(this, getString(R.string.dialog_history_cleared), Toast.LENGTH_SHORT).show()
                }
            }
            getString(R.string.menu_logout) -> {
                showConfirmDialog(
                    getString(R.string.dialog_logout),
                    getString(R.string.dialog_logout_confirm)
                ) {
                    logout()
                }
            }
        }
    }

    private fun showConfirmDialog(title: String, message: String, onConfirm: () -> Unit) {
        AlertDialog.Builder(this)
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton(getString(R.string.dialog_yes)) { _, _ -> onConfirm() }
            .setNegativeButton(getString(R.string.dialog_no), null)
            .show()
    }

    // ✅ ADD YE 2 FUNCTIONS
    private fun showLanguageSelectionDialog() {
        val languages = arrayOf("English", "हिंदी", "नेपाली")
        val langCodes = arrayOf("en", "hi", "ne")

        android.app.AlertDialog.Builder(this)
            .setTitle("Select Language")
            .setItems(languages) { _, which ->
                setLanguage(langCodes[which])
            }
            .show()
    }

    private fun setLanguage(langCode: String) {
        val prefs = getSharedPreferences("AppSettings", MODE_PRIVATE)
        prefs.edit().putString("language", langCode).apply()

        LocaleHelper.setLocale(this, langCode)

        // ✅ ProfileActivity recreate kar
        recreate()

        // ✅ Optional: MainActivity ko refresh karne ke liye
        sendBroadcast(Intent("LANGUAGE_CHANGED"))

        Toast.makeText(this, "Language changed", Toast.LENGTH_SHORT).show()
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