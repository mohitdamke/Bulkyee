package com.example.bulkyee.viewmodel

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bulkyee.data.PreferencesHelper
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class ProfileViewModel : ViewModel() {

    private val db = FirebaseFirestore.getInstance()
    private val firebaseUser = FirebaseAuth.getInstance().currentUser
    private val userId = firebaseUser!!.uid

    // Save user details to Firestore

    fun saveUserDetails(
        context: Context,
        name: String,
        shopName: String,
        phoneNumber: String,
        address: String,
        email: String
    ) {
        val firebaseUser = FirebaseAuth.getInstance().currentUser
        val userId = firebaseUser!!.uid
        val userMap = hashMapOf(
            "name" to name,
            "shopName" to shopName,
            "phoneNumber" to phoneNumber,
            "address" to address,
            "email" to email,
        )

        val db = FirebaseFirestore.getInstance()
        db.collection("users").document(userId)
            .set(userMap)
            .addOnSuccessListener {
                // Details saved successfully
                Toast.makeText(context, "Details saved to Firestore", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                // Handle error
                Toast.makeText(context, "Error saving details: ${e.message}", Toast.LENGTH_SHORT)
                    .show()
            }
    }

    // Fetch user details from Firestore
    fun fetchUserDetails(
        context: Context,
        onSuccess: (
            name: String,
            shopName: String,
            phoneNumber: String,
            address: String,
            email: String

        ) -> Unit
    ) {
        val firebaseUser = FirebaseAuth.getInstance().currentUser
        val userId = firebaseUser!!.uid
        val db = FirebaseFirestore.getInstance()

        db.collection("users").document(userId)
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    // Retrieve and use the user details from Firestore
                    val name = document.getString("name") ?: ""
                    val shopName = document.getString("shopName") ?: ""
                    val phoneNumber = document.getString("phoneNumber") ?: ""
                    val address = document.getString("address") ?: ""
                    val email = document.getString("email") ?: ""
                    onSuccess(name, shopName, phoneNumber, address, email)
                } else {
                    // No user details found, you can show a message or navigate to the information screen
                    Toast.makeText(context, "User details not found", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { e ->
                // Handle failure
                Toast.makeText(
                    context,
                    "Error fetching user details: ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
    }


    // Function to fetch profile data from Firestore
    suspend fun fetchUserProfile(): Map<String, String> {
        val userMap = mutableMapOf<String, String>()
        try {
            val document = db.collection("users").document(userId).get().await()
            if (document.exists()) {
                userMap["name"] = document.getString("name") ?: ""
                userMap["shopName"] = document.getString("shopName") ?: ""
                userMap["phoneNumber"] = document.getString("phoneNumber") ?: ""
                userMap["address"] = document.getString("address") ?: ""
            }
        } catch (e: Exception) {
            // Handle any exception
        }
        return userMap
    }

    // Function to update profile data
    fun updateProfile(
        name: String,
        shopName: String,
        phoneNumber: String,
        address: String,
        context: Context,
        onSuccess: () -> Unit,
        onFailure: (String) -> Unit
    ) {
        val userMap = hashMapOf(
            "name" to name,
            "shopName" to shopName,
            "phoneNumber" to phoneNumber,
            "address" to address
        )

        viewModelScope.launch {
            try {
                db.collection("users").document(userId).set(userMap).await()
                PreferencesHelper.saveUserInfo(
                    context = context,
                    name = name,
                    shopName = shopName,
                    phoneNumber = phoneNumber,
                    address = address,
                    email = firebaseUser?.email ?: "No Email"
                )
                onSuccess()
            } catch (e: Exception) {
                onFailure(e.message ?: "Error updating profile")
            }
        }
    }
}