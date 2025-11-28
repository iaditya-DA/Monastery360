package com.example.monastery360

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class EditProfileActivity : AppCompatActivity() {

    private lateinit var backButton: ImageView
    private lateinit var firstNameEdit: EditText
    private lateinit var lastNameEdit: EditText
    private lateinit var usernameEdit: EditText
    private lateinit var emailEdit: EditText
    private lateinit var phoneEdit: EditText
    private lateinit var saveButton: Button
    private lateinit var changePasswordButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)

        backButton = findViewById(R.id.backButtonEditProfile)
        firstNameEdit = findViewById(R.id.firstNameEdit)
        lastNameEdit = findViewById(R.id.lastNameEdit)
        usernameEdit = findViewById(R.id.usernameEdit)
        emailEdit = findViewById(R.id.emailEdit)
        phoneEdit = findViewById(R.id.phoneEdit)
        saveButton = findViewById(R.id.saveButton)
        changePasswordButton = findViewById(R.id.changePasswordButton)

        // Load current data (dummy)
        firstNameEdit.setText("Sabrina")
        lastNameEdit.setText("Aryan")
        usernameEdit.setText("Sabrina")
        emailEdit.setText("SabrinaAry208@gmail.com")
        phoneEdit.setText("+91 9048470")

        backButton.setOnClickListener {
            finish()
        }

        saveButton.setOnClickListener {
            saveProfile()
        }

        changePasswordButton.setOnClickListener {
            Toast.makeText(this, "Change Password", Toast.LENGTH_SHORT).show()
        }
    }

    private fun saveProfile() {
        val firstName = firstNameEdit.text.toString()
        val lastName = lastNameEdit.text.toString()
        val username = usernameEdit.text.toString()
        val email = emailEdit.text.toString()
        val phone = phoneEdit.text.toString()

        if (firstName.isEmpty() || lastName.isEmpty() || email.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
            return
        }

        // Save to SharedPreferences
        val prefs = getSharedPreferences("UserProfile", MODE_PRIVATE)
        prefs.edit().apply {
            putString("firstName", firstName)
            putString("lastName", lastName)
            putString("username", username)
            putString("email", email)
            putString("phone", phone)
            apply()
        }

        Toast.makeText(this, "Profile saved successfully", Toast.LENGTH_SHORT).show()
        finish()
    }
}