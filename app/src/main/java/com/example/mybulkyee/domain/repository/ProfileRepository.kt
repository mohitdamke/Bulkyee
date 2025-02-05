package com.example.mybulkyee.domain.repository

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class ProfileRepository @Inject constructor(
    private val db: FirebaseFirestore,
    private val auth: FirebaseAuth
) {

    suspend fun saveUserDetails(userMap: Map<String, Any>): Boolean {
        val userId = auth.currentUser?.uid ?: return false
        return try {
            db.collection("users").document(userId).set(userMap).await()
            true
        } catch (e: Exception) {
            Log.e("ProfileRepository", "Error saving user details: ${e.message}")
            false
        }
    }

    suspend fun fetchUserDetails(): Map<String, String> {
        val userId = auth.currentUser?.uid ?: return emptyMap()
        return try {
            val document = db.collection("users").document(userId).get().await()
            if (document.exists()) {
                mapOf(
                    "name" to (document.getString("name") ?: ""),
                    "shopName" to (document.getString("shopName") ?: ""),
                    "phoneNumber" to (document.getString("phoneNumber") ?: ""),
                    "address" to (document.getString("address") ?: ""),
                    "email" to (document.getString("email") ?: "")
                )
            } else emptyMap()
        } catch (e: Exception) {
            Log.e("ProfileRepository", "Error fetching profile: ${e.message}")
            emptyMap()
        }
    }

    suspend fun updateProfile(userMap: Map<String, Any>): Boolean {
        val userId = auth.currentUser?.uid ?: return false
        return try {
            db.collection("users").document(userId).set(userMap).await()
            true
        } catch (e: Exception) {
            Log.e("ProfileRepository", "Error updating profile: ${e.message}")
            false
        }
    }
}
