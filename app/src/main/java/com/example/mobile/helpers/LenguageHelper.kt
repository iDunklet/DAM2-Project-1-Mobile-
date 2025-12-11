package com.example.mobile.helpers

import android.content.Context
import java.util.Locale

object LanguageHelper {

    // Returns a context configured with the chosen locale
    fun wrapContext(context: Context, language: String): Context {
        val locale = Locale(language)
        Locale.setDefault(locale)

        val config = context.resources.configuration
        config.setLocale(locale)
        // For layout direction (e.g., RTL languages), uncomment:
        // config.setLayoutDirection(locale)

        return context.createConfigurationContext(config)
    }

    // Saves selected language
    fun saveLanguagePref(context: Context, language: String) {
        val prefs = context.getSharedPreferences("AppSettings", Context.MODE_PRIVATE)
        prefs.edit().putString("language", language).apply()
    }

    // Loads saved language (default: Spanish)
    fun loadLanguagePref(context: Context): String {
        val prefs = context.getSharedPreferences("AppSettings", Context.MODE_PRIVATE)
        return prefs.getString("language", "es") ?: "es"
    }
}