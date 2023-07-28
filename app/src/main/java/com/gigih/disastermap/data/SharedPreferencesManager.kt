package com.gigih.disastermap.data

import android.content.Context
import androidx.appcompat.app.AppCompatDelegate

object SharedPreferencesManager {

    private const val PREFS_NAME = "ThemePrefs"
    private const val THEME_KEY = "theme_key"
    private const val DEFAULT_THEME_MODE = AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM

    fun getThemeMode(context: Context): Int {
        val sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return sharedPreferences.getInt(THEME_KEY, DEFAULT_THEME_MODE)
    }

    fun setThemeMode(context: Context, themeMode: Int) {
        val sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        sharedPreferences.edit().putInt(THEME_KEY, themeMode).apply()
    }
}