package com.example.monastery360

import android.app.Application
import com.example.monastery360.utils.LocaleHelper

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        LocaleHelper.setLocale(this, LocaleHelper.getSavedLanguage(this))
    }
}
