package com.example.miiproyecto1.utils

import android.content.Context

object SessionManager {
    private const val PREF_NAME = "app_session"
    private const val KEY_LOGGED_IN = "is_logged_in"

    fun setLoggedIn(context: Context, value: Boolean) {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        prefs.edit().putBoolean(KEY_LOGGED_IN, value).apply()
    }

    fun isLoggedIn(context: Context): Boolean {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        return prefs.getBoolean(KEY_LOGGED_IN, false)
    }
}