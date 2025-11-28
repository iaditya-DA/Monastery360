package com.example.rideo // 🔥 Use your actual package name

import android.animation.AnimatorInflater
import android.animation.AnimatorSet
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.google.firebase.FirebaseApp // ✅ Firebase import

class SplashActivity : AppCompatActivity() {

    // Duration of splash screen (2 seconds)
    private val SPLASH_DELAY: Long = 2000

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // ✅ Initialize Firebase as soon as splash starts
        FirebaseApp.initializeApp(this)

        // Set your splash layout
        setContentView(R.layout.activity_onboarding)

        // Start animations
        animateLogoAndText()

        // Navigate to next screen after delay
        Handler(Looper.getMainLooper()).postDelayed({
            val intent = Intent(this, RoleSelectionActivity::class.java)
            startActivity(intent)
            finish() // prevent going back to splash
        }, SPLASH_DELAY)
    }

    private fun animateLogoAndText() {
        // Animate logo (CardView)
        val logoContainer = findViewById<CardView>(R.id.logo_container)
        val logoAnimator = AnimatorInflater.loadAnimator(
            this,
            R.animator.logo_pop_animation // Your pop animation XML
        ) as AnimatorSet
        logoAnimator.setTarget(logoContainer)
        logoAnimator.start()

        // Animate text (slide up)
        val appNameText = findViewById<TextView>(R.id.app_name_text)
        val taglineText = findViewById<TextView>(R.id.tagline_text)
        val textAnimator = AnimatorInflater.loadAnimator(
            this,
            R.animator.text_slide_up // Your text animation XML
        ) as AnimatorSet

        textAnimator.startDelay = 200 // small delay for better effect

        textAnimator.setTarget(appNameText)
        textAnimator.start()

        // Start a separate animation for tagline to ensure both animate smoothly
        val taglineAnimator = AnimatorInflater.loadAnimator(
            this,
            R.animator.text_slide_up
        ) as AnimatorSet
        taglineAnimator.startDelay = 400
        taglineAnimator.setTarget(taglineText)
        taglineAnimator.start()
    }
}
