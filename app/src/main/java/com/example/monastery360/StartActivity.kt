package com.example.monastery360

import android.content.Intent
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import com.example.monastery360.utils.LocaleHelper


class StartActivity : BaseActivity() {

    private var startY = 0f
    private var originalY = 0f
    private val SWIPE_DISTANCE = 250f  // Kitna upar le jana hoga

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.start_screen)

        supportActionBar?.hide()

        val btnGo = findViewById<LinearLayout>(R.id.btnGo)
        val bgImage = findViewById<ImageView>(R.id.bgImage)

        btnGo.post {
            originalY = btnGo.y
        }

        btnGo.setOnTouchListener { view, event ->

            when (event.action) {

                MotionEvent.ACTION_DOWN -> {
                    startY = event.rawY
                    true
                }

                MotionEvent.ACTION_MOVE -> {
                    val diff = event.rawY - startY

                    if (diff < 0) {
                        btnGo.y = originalY + diff   // only button moves
                    }
                    true
                }

                MotionEvent.ACTION_UP -> {
                    val totalMove = originalY - btnGo.y

                    if (totalMove > SWIPE_DISTANCE) {
                        val intent = Intent(this, LoginActivity::class.java)
                        startActivity(intent)
                        finish()
                    } else {
                        btnGo.animate().y(originalY).setDuration(250).start()
                    }
                    true
                }

                else -> false
            }
        }

    }
}
