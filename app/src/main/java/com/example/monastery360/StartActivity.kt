package com.example.monastery360

import android.content.Intent
import android.os.Bundle
import android.view.MotionEvent
import android.widget.LinearLayout

class StartActivity : BaseActivity() {

    private var startY = 0f
    private var originalY = 0f
    private val SWIPE_DISTANCE = 250f

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.start_screen)

        supportActionBar?.hide()

        // ✅ Poora button container (arrow + GO circle)
        val btnGo = findViewById<LinearLayout>(R.id.btnGo)

        btnGo.post {
            originalY = btnGo.y
        }

        // ✅ Touch listener on button
        btnGo.setOnTouchListener { view, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    startY = event.rawY
                    btnGo.animate().cancel()
                    true
                }

                MotionEvent.ACTION_MOVE -> {
                    val diff = event.rawY - startY

                    if (diff < 0) {
                        btnGo.y = originalY + diff
                    }
                    true
                }

                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                    val totalMove = originalY - btnGo.y

                    if (totalMove > SWIPE_DISTANCE) {
                        navigateToLogin()
                    } else {
                        btnGo.animate()
                            .y(originalY)
                            .setDuration(250)
                            .start()
                    }
                    true
                }

                else -> false
            }
        }
    }

    private fun navigateToLogin() {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        finish()
    }
}