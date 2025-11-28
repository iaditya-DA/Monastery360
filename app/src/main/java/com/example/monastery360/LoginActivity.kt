package com.example.monastery360

import android.animation.ObjectAnimator
import android.animation.AnimatorSet
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
import com.example.monastery360.utils.LocaleHelper


class LoginActivity : BaseActivity() {

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

    // Hardcoded credentials
    private val VALID_EMAIL = "admin@monastery.com"
    private val VALID_PASSWORD = "12345"

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

        // Hide action bar
        supportActionBar?.hide()

        // Initialize background images
        bg1 = findViewById(R.id.background_image1)
        bg2 = findViewById(R.id.background_image2)

        bg1.setImageResource(images[0])
        bg2.visibility = View.GONE

        // Start background slideshow
        handler.postDelayed(runnable, 2000)

        // Set click listeners
        findViewById<Button>(R.id.login_button).setOnClickListener {
            showLoginSheet()
        }

        findViewById<TextView>(R.id.signup_text_button).setOnClickListener {
            showRegisterSheet()
        }
    }

    private fun showLoginSheet() {
        val view = layoutInflater.inflate(R.layout.bottom_login_sheet, null)
        val dialog = BottomSheetDialog(this, R.style.BottomSheetTheme)
        dialog.setContentView(view)
        dialog.show()

        val email = view.findViewById<TextInputEditText>(R.id.et_email)
        val pass = view.findViewById<TextInputEditText>(R.id.et_password)

        view.findViewById<Button>(R.id.btn_bottom_sheet_login).setOnClickListener {
            val emailText = email.text.toString().trim()
            val passText = pass.text.toString().trim()

            // Validation
            if (emailText.isEmpty()) {
                email.error = "Enter email"
                return@setOnClickListener
            }
            if (passText.isEmpty()) {
                pass.error = "Enter password"
                return@setOnClickListener
            }

            // Hardcoded login check
            if (emailText == VALID_EMAIL && passText == VALID_PASSWORD) {
                Toast.makeText(this, "Login Successful! ✓", Toast.LENGTH_SHORT).show()
                dialog.dismiss()

                // Navigate to MainActivity
                navigateToMainActivity()
            } else {
                Toast.makeText(this, "Invalid credentials! Try: $VALID_EMAIL / $VALID_PASSWORD", Toast.LENGTH_LONG).show()
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

        view.findViewById<Button>(R.id.btn_register).setOnClickListener {
            val nameText = name.text.toString().trim()
            val emailText = email.text.toString().trim()
            val passText = pass.text.toString().trim()
            val confirmPassText = confirmPass.text.toString().trim()

            // Validation
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
            if (confirmPassText.isEmpty()) {
                confirmPass.error = "Confirm password"
                return@setOnClickListener
            }
            if (passText != confirmPassText) {
                confirmPass.error = "Passwords don't match"
                return@setOnClickListener
            }

            // Registration successful (hardcoded)
            Toast.makeText(this, "Registration Successful! Please login.", Toast.LENGTH_SHORT).show()
            dialog.dismiss()

            // Show login sheet
            showLoginSheet()
        }

        // Optional: Social login icons
        view.findViewById<ImageView>(R.id.iv_google_register)?.setOnClickListener {
            Toast.makeText(this, "Google login coming soon!", Toast.LENGTH_SHORT).show()
        }

        view.findViewById<ImageView>(R.id.iv_facebook_register)?.setOnClickListener {
            Toast.makeText(this, "Facebook login coming soon!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun navigateToMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish() // Close login activity so user can't go back
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacks(runnable)
    }
}