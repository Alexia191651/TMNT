package com.example.tenantmanagementsystem

import android.content.Context

class SessionManager(context: Context) {
    private val prefs = context.getSharedPreferences("session", Context.MODE_PRIVATE)
    fun login(email: String, name: String) = prefs.edit().putString("email", email).putString("name", name).apply()
    fun isLoggedIn() = prefs.getString("email", null) != null
    fun logout() = prefs.edit().clear().apply()
}