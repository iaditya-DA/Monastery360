package com.example.rideo

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class RiderLoginActivity : AppCompatActivity() {

    // Firebase instances
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    // View declarations
    private lateinit var inputEmail: EditText
    private lateinit var inputPassword: EditText
    private lateinit var btnLogin: Button
    private lateinit var loginSubtitle: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_rider_login)

        // Initialize Firebase
        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        // --- View Initialization ---
        inputEmail = findViewById(R.id.input_email)
        inputPassword = findViewById(R.id.input_password)
        btnLogin = findViewById(R.id.btn_login_rider)
        val backButton = findViewById<ImageView>(R.id.back_button)
        loginSubtitle = findViewById(R.id.login_subtitle)

        // --- Edge-to-Edge setup ---
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // --- Back Navigation ---
        backButton.setOnClickListener {
            // Returns to the RoleSelectionActivity
            finish()
        }

        // --- Login Logic ---
        btnLogin.setOnClickListener {
            val email = inputEmail.text.toString().trim()
            val password = inputPassword.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                showMessage("Error: Please enter both email/ID and password.", android.R.color.holo_red_light)
                return@setOnClickListener
            }

            // Start Firebase Authentication
            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // 1. Authentication successful, now verify the role
                        verifyUserRole(auth.currentUser!!.uid)
                    } else {
                        // 2. Authentication failed (wrong password/email)
                        showMessage("Error: Invalid email or password.", android.R.color.holo_red_light)
                    }
                }
        }
    }

    /**
     * Checks Firestore to ensure the user has the 'RIDER' role (Feature F3).
     */
    private fun verifyUserRole(uid: String) {
        db.collection("users").document(uid).get()
            .addOnSuccessListener { document ->
                val role = document.getString("role")

                if (role == "RIDER") {
                    // Success: Correct role found. Redirect to Rider Dashboard.
                    showMessage("Login successful! Redirecting...", android.R.color.holo_green_light)

                    val intent = Intent(this, MainActivity::class.java) // MainActivity is the Rider Dashboard
                    startActivity(intent)
                    finish()
                } else {
                    // Failed: User exists but has the wrong role (e.g., they are a DRIVER trying to use the RIDER app)
                    showMessage("Access Denied. You are logged in as a $role.", android.R.color.holo_red_light)
                    auth.signOut() // Force logout to clear session
                }
            }
            .addOnFailureListener {
                // Failed: Could not fetch user data (user document missing)
                showMessage("Login error: User profile not found in database.", android.R.color.holo_red_light)
                auth.signOut()
            }
    }

    /**
     * Utility function to update the subtitle text and color.
     */
    private fun showMessage(message: String, colorResId: Int) {
        loginSubtitle.text = message
        loginSubtitle.setTextColor(ContextCompat.getColor(this, colorResId))
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}