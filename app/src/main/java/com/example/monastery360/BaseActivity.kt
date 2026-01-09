package com.example.monastery360

import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.monastery360.utils.LocaleHelper

abstract class BaseActivity : AppCompatActivity() {

    override fun attachBaseContext(newBase: Context) {
        val langCode = LocaleHelper.getSavedLanguage(newBase)
        val context = if (langCode.isNotEmpty()) {
            LocaleHelper.setLocale(newBase, langCode)
        } else {
            newBase
        }
        super.attachBaseContext(context)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val langCode = LocaleHelper.getSavedLanguage(this)
        if (langCode.isNotEmpty()) {
            applyLocale(langCode)
        }
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        val langCode = LocaleHelper.getSavedLanguage(this)
        if (langCode.isNotEmpty()) {
            applyLocale(langCode)
        }
    }

    private fun applyLocale(language: String) {
        val locale = java.util.Locale(language)
        java.util.Locale.setDefault(locale)
        val config = Configuration(resources.configuration)
        config.setLocale(locale)

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            config.setLocales(android.os.LocaleList(locale))
        }

        resources.updateConfiguration(config, resources.displayMetrics)
    }
}