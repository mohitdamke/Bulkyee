package com.example.bulkyee.data

import android.content.Context
import android.content.SharedPreferences

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
        email: String
    ) {
        val editor = getPreferences(context).edit()
        editor.putString(KEY_NAME, name)
        editor.putString(KEY_SHOP_NAME, shopName)
        editor.putString(KEY_PHONE_NUMBER, phoneNumber)
        editor.putString(KEY_ADDRESS, address)
        editor.putString(KEY_EMAIL, email)
        editor.putBoolean(KEY_USER_SETUP_COMPLETED, true)  // Flag to indicate user setup is complete
        editor.apply() // Commit changes
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
        return getPreferences(context).getBoolean(KEY_USER_SETUP_COMPLETED, false)
    }
}


//  To retrieve the information

//val userInfo = PreferencesHelper.getUserInfo(context)
//val name = userInfo["name"]
//val shopName = userInfo["shopName"]
//val phoneNumber = userInfo["phoneNumber"]
//val address = userInfo["address"]
//val email = userInfo["email"]
