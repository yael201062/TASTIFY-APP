package com.example.tastify.util

import android.content.Context
import android.widget.Toast
import androidx.navigation.NavController
import com.example.tastify.util.AuthManager

object LogoutHelper {
    fun performLogout(context: Context, navController: NavController) {
        AuthManager.logout(context)
        Toast.makeText(context, "התנתקת בהצלחה", Toast.LENGTH_SHORT).show()
        navController.navigate(com.example.tastify.R.id.loginFragment)
    }
}
