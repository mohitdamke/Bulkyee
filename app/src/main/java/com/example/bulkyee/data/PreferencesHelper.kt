package com.example.bulkyee.data

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.google.firebase.auth.FirebaseAuth

object PreferencesHelper {
    private const val PREF_NAME = "user_preferences"
    private const val KEY_NAME = "user_name"
    private const val KEY_SHOP_NAME = "shop_name"
    private const val KEY_PHONE_NUMBER = "phone_number"
    private const val KEY_ADDRESS = "address"
    private const val KEY_EMAIL = "email"
    private const val KEY_USER_SETUP_COMPLETED = "user_setup_completed"

    // Get SharedPreferences instance
    private fun getPreferences(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    }

    // Save user information to SharedPreferences
    fun saveUserInfo(
        context: Context,
        name: String,
        shopName: String,
        phoneNumber: String,
        address: String,
        email: String,
        keyUser: Boolean,
    ) {
        val editor = getPreferences(context).edit()
        editor.putString(KEY_NAME, name)
        editor.putString(KEY_SHOP_NAME, shopName)
        editor.putString(KEY_PHONE_NUMBER, phoneNumber)
        editor.putString(KEY_ADDRESS, address)
        editor.putString(KEY_EMAIL, email)
        editor.putBoolean(KEY_USER_SETUP_COMPLETED, keyUser)  // Flag to indicate user setup is complete
        editor.apply() // Commit changes
    }

    fun logoutInfo(context: Context) {
        val preferences = getPreferences(context)
        val editor = preferences.edit()

        // Reset all user-related preferences
        editor.putString(KEY_NAME, "")
        editor.putString(KEY_SHOP_NAME, "")
        editor.putString(KEY_PHONE_NUMBER, "")
        editor.putString(KEY_ADDRESS, "")
        editor.putString(KEY_EMAIL, "")
        editor.putBoolean(KEY_USER_SETUP_COMPLETED, false)  // Flag to indicate user setup is complete

        // Commit changes to SharedPreferences
        editor.apply()

        // Optionally, clear any other stored data if necessary (like auth tokens)

        // Sign out from Firebase
        FirebaseAuth.getInstance().signOut()

        // Log out completed
        Log.d("PreferencesHelper", "User logged out and data reset successfully.")
    }


    // Retrieve user information from SharedPreferences
    fun getUserInfo(context: Context): Map<String, String> {
        val preferences = getPreferences(context)
        val name = preferences.getString(KEY_NAME, "") ?: ""
        val shopName = preferences.getString(KEY_SHOP_NAME, "") ?: ""
        val phoneNumber = preferences.getString(KEY_PHONE_NUMBER, "") ?: ""
        val address = preferences.getString(KEY_ADDRESS, "") ?: ""
        val email = preferences.getString(KEY_EMAIL, "") ?: ""
        return mapOf(
            "name" to name,
            "shopName" to shopName,
            "phoneNumber" to phoneNumber,
            "address" to address,
            "email" to email
        )
    }

    // Check if the user has completed the setup
    fun isUserSetupCompleted(context: Context): Boolean {
        return try {
            getPreferences(context).getBoolean(KEY_USER_SETUP_COMPLETED, false)
        } catch (e: Exception) {
            Log.e("PreferencesHelper", "Error fetching setup completion", e)
            false
        }
    }

    // Clear user setup status
    fun clearUserSetupStatus(context: Context) {
        try {
            getPreferences(context)
                .edit()
                .putBoolean(KEY_USER_SETUP_COMPLETED, false)  // Set to false on logout
                .apply()
        } catch (e: Exception) {
            Log.e("PreferencesHelper", "Error clearing setup status", e)
        }
    }
}
