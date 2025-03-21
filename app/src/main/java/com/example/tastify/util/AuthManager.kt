package com.example.tastify.util

import android.content.Context
import com.google.firebase.auth.FirebaseAuth

object AuthManager {

    private const val PREFS_NAME = "auth_prefs"
    private const val KEY_USER_UID = "user_uid"

    fun saveUserUid(context: Context, uid: String) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putString(KEY_USER_UID, uid).apply()
    }

    fun getUserUid(context: Context): String? {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getString(KEY_USER_UID, null)
    }

    fun clearUserUid(context: Context) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().remove(KEY_USER_UID).apply()
    }

    fun logout(context: Context) {
        FirebaseAuth.getInstance().signOut()
        clearUserUid(context)
    }
}