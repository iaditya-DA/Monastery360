package com.example.monastery360

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import com.example.monastery360.utils.LocaleHelper

class LanguageSelectionActivity : BaseActivity() {

    private lateinit var btnEnglish: Button
    private lateinit var btnHindi: Button
    private lateinit var btnNepali: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.language_selection)

        btnEnglish = findViewById(R.id.btnEnglish)
        btnHindi = findViewById(R.id.btnHindi)
        btnNepali = findViewById(R.id.btnNepali)

        btnEnglish.setOnClickListener { selectLanguage("en") }
        btnHindi.setOnClickListener { selectLanguage("hi") }
        btnNepali.setOnClickListener { selectLanguage("ne") }
    }

    private fun selectLanguage(langCode: String) {
        LocaleHelper.setLocale(this, langCode)
        startActivity(Intent(this, StartActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
        })
        finish()
    }
}
