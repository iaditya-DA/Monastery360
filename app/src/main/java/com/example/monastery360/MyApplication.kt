package com.example.monastery360

import android.app.Application
import android.content.Context
import com.example.monastery360.utils.LocaleHelper

class MyApplication : Application() {

    //  Flickering fixed
    override fun attachBaseContext(base: Context) {
        val langCode = LocaleHelper.getSavedLanguage(base)
        val context = if (langCode.isNotEmpty()) {
            LocaleHelper.setLocale(base, langCode)
        } else {
            base
        }
        super.attachBaseContext(context)
    }

    override fun onCreate() {
        super.onCreate()

        // Apply saved language on app start
        val langCode = LocaleHelper.getSavedLanguage(this)
        if (langCode.isNotEmpty()) {
            LocaleHelper.setLocale(this, langCode)
        }
    }
}