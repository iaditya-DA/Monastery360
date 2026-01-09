package com.example.monastery360

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class LoginActivity : AppCompatActivity() {

    private lateinit var bg1: ImageView
    private lateinit var bg2: ImageView

    private var index = 0
    private var showingFirst = true
    private var slideFromLeft = true

    private val images = arrayOf(
        R.drawable.bg1,
        R.drawable.bg2,
        R.drawable.bg3,
        R.drawable.bg4
    )

    private val handler = Handler(Looper.getMainLooper())

    // ✅ Firebase Auth instance
    private lateinit var auth: FirebaseAuth

    private val runnable = object : Runnable {
        override fun run() {
            val incoming = if (showingFirst) bg2 else bg1
            val outgoing = if (showingFirst) bg1 else bg2

            incoming.setImageResource(images[index])
            incoming.visibility = View.VISIBLE

            if (slideFromLeft) {
                incoming.translationX = -incoming.width.toFloat()
                outgoing.translationX = 0f
            } else {
                incoming.translationX = incoming.width.toFloat()
                outgoing.translationX = 0f
            }

            val animOut = if (slideFromLeft)
                ObjectAnimator.ofFloat(outgoing, "translationX", 0f, outgoing.width.toFloat())
            else
                ObjectAnimator.ofFloat(outgoing, "translationX", 0f, -outgoing.width.toFloat())

            val animIn = ObjectAnimator.ofFloat(incoming, "translationX", incoming.translationX, 0f)

            AnimatorSet().apply {
                playTogether(animOut, animIn)
                duration = 800
                start()
            }

            outgoing.postDelayed({
                outgoing.visibility = View.GONE
            }, 800)

            index = (index + 1) % images.size
            showingFirst = !showingFirst
            slideFromLeft = !slideFromLeft

            handler.postDelayed(this, 3000)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        supportActionBar?.hide()

        // ✅ Firebase Auth ko initialize karein
        auth = Firebase.auth

        // ✅ Check karein ki user pehle se logged in hai ya nahi
        if (auth.currentUser != null) {
            navigateToMainActivity()
            return // Baaki ka code execute na karein
        }

        // --- Baaki ka setup waisa hi rahega ---
        bg1 = findViewById(R.id.background_image1)
        bg2 = findViewById(R.id.background_image2)
        bg1.setImageResource(images[0])
        bg2.visibility = View.GONE
        handler.postDelayed(runnable, 2000)
        findViewById<Button>(R.id.login_button).setOnClickListener { showLoginSheet() }
        findViewById<TextView>(R.id.signup_text_button).setOnClickListener { showRegisterSheet() }
    }

    private fun showLoginSheet() {
        val view = layoutInflater.inflate(R.layout.bottom_login_sheet, null)
        val dialog = BottomSheetDialog(this, R.style.BottomSheetTheme)
        dialog.setContentView(view)
        dialog.show()

        val email = view.findViewById<TextInputEditText>(R.id.et_email)
        val pass = view.findViewById<TextInputEditText>(R.id.et_password)
        val loginButton = view.findViewById<Button>(R.id.btn_bottom_sheet_login)

        loginButton.setOnClickListener {
            val emailText = email.text.toString().trim()
            val passText = pass.text.toString().trim()

            if (emailText.isEmpty()) {
                email.error = "Enter email"
                return@setOnClickListener
            }
            if (passText.isEmpty()) {
                pass.error = "Enter password"
                return@setOnClickListener
            }

            // ✅ Firebase se Login karein
            loginButton.text = "Logging In..." // UI update
            loginButton.isEnabled = false

            auth.signInWithEmailAndPassword(emailText, passText)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // Login successful
                        Toast.makeText(this, "Login Successful! ✓", Toast.LENGTH_SHORT).show()
                        dialog.dismiss()
                        navigateToMainActivity()
                    } else {
                        // Login failed
                        Toast.makeText(this, "Authentication Failed: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                        loginButton.text = "Login" // Reset button
                        loginButton.isEnabled = true
                    }
                }
        }
    }

    private fun showRegisterSheet() {
        val view = layoutInflater.inflate(R.layout.bottom_register_sheet, null)
        val dialog = BottomSheetDialog(this, R.style.BottomSheetTheme)
        dialog.setContentView(view)
        dialog.show()

        val name = view.findViewById<TextInputEditText>(R.id.et_name)
        val email = view.findViewById<TextInputEditText>(R.id.et_email)
        val pass = view.findViewById<TextInputEditText>(R.id.et_password)
        val confirmPass = view.findViewById<TextInputEditText>(R.id.et_confirm_password)
        val registerButton = view.findViewById<Button>(R.id.btn_register)

        registerButton.setOnClickListener {
            val nameText = name.text.toString().trim()
            val emailText = email.text.toString().trim()
            val passText = pass.text.toString().trim()
            val confirmPassText = confirmPass.text.toString().trim()

            // --- VALIDATION SECTION ---
            if (nameText.isEmpty()) {
                name.error = "Enter name"
                return@setOnClickListener
            }
            if (emailText.isEmpty()) {
                email.error = "Enter email"
                return@setOnClickListener
            }
            if (passText.isEmpty()) {
                pass.error = "Enter password"
                return@setOnClickListener
            }
            // ✅ ADDED: Password length validation
            if (passText.length < 6) {
                pass.error = "Password must be at least 6 characters"
                return@setOnClickListener
            }
            if (confirmPassText.isEmpty()) {
                confirmPass.error = "Confirm password"
                return@setOnClickListener
            }
            if (passText != confirmPassText) {
                confirmPass.error = "Passwords don't match"
                return@setOnClickListener
            }

            // --- FIREBASE REGISTRATION ---
            registerButton.text = "Registering..."
            registerButton.isEnabled = false

            auth.createUserWithEmailAndPassword(emailText, passText)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // Registration successful
                        Toast.makeText(this, "Registration Successful! Please login.", Toast.LENGTH_LONG).show()
                        dialog.dismiss()
                        showLoginSheet() // User ko login sheet par bhej dein
                    } else {
                        // Registration failed
                        Toast.makeText(this, "Registration Failed: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                        registerButton.text = "Register" // Reset button
                        registerButton.isEnabled = true
                    }
                }
        }
    }

    private fun navigateToMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        // Back stack clear kar dein taaki user login screen par wapas na aa sake
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacks(runnable)
    }
}