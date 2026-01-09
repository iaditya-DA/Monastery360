package com.example.monastery360

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.example.monastery360.utils.LocaleHelper

/**
 * Yeh activity sirf language select karne aur save karne ke liye hai.
 * Yeh AppCompatActivity se inherit karti hai, BaseActivity se nahi, taaki flicker na ho.
 */
class LanguageSelectionActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // ✅ Best Practice: Agar user ne pehle hi language select kar li hai,
        // to use baar-baar yeh screen na dikhayein.
        if (LocaleHelper.getSavedLanguage(this).isNotEmpty()) {
            navigateToStart()
            return // Aage ka code execute na karein
        }

        setContentView(R.layout.language_selection)

        // Buttons ko initialize karein
        val btnEnglish: CardView = findViewById(R.id.btnEnglish)
        val btnHindi: CardView = findViewById(R.id.btnHindi)
        val btnNepali: CardView = findViewById(R.id.btnNepali)
        val btnBengali: CardView = findViewById(R.id.btnBengali)

        // Click listeners set karein
        btnEnglish.setOnClickListener { selectLanguage("en", it as CardView) }
        btnHindi.setOnClickListener { selectLanguage("hi", it as CardView) }
        btnNepali.setOnClickListener { selectLanguage("ne", it as CardView) }
        btnBengali.setOnClickListener { selectLanguage("bn", it as CardView) }
    }

    private fun selectLanguage(langCode: String, clickedView: CardView) {
        // Double-click se bachne ke liye button ko disable kar dein
        clickedView.isEnabled = false

        // ✅ Sahi Tarika: Is activity ka kaam sirf language ko 'save' karna hai.
        // 'Apply' karne ka kaam BaseActivity khud karegi agli screens par.
        LocaleHelper.saveLanguage(this, langCode)

        // Agli screen par navigate karein
        navigateToStart()
    }

    private fun navigateToStart() {
        val intent = Intent(this, StartActivity::class.java).apply {
            // Nayi activity shuru hone par purani sabhi activities ko stack se clear kar dein
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        startActivity(intent)

        // ✅ Flicker rokne ke liye transition animation ko band kar dein
        overridePendingTransition(0, 0)

        // Is activity ko band kar dein
        finish()
    }
}
