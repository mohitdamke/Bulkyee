package com.store.mybulkyee.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.store.mybulkyee.domain.repository.ProfileRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(private val repository: ProfileRepository) : ViewModel() {

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _userProfile = MutableStateFlow<Map<String, String>?>(null)
    val userProfile: StateFlow<Map<String, String>?> = _userProfile

    private val _isSuccess = MutableStateFlow(false)
    val isSuccess: StateFlow<Boolean> = _isSuccess

    private val db = FirebaseFirestore.getInstance()
    private val firebaseUser = FirebaseAuth.getInstance().currentUser

    private val _isError = MutableStateFlow<String?>(null)
    val isError: StateFlow<String?> = _isError

    fun saveUserDetails(
        name: String,
        shopName: String,
        phoneNumber: String,
        address: String,
        email: String
    ) {
        val userMap = mapOf(
            "name" to name,
            "shopName" to shopName,
            "phoneNumber" to phoneNumber,
            "address" to address,
            "email" to email
        )

        viewModelScope.launch(Dispatchers.IO) {
            _isLoading.value = true
            val result = repository.saveUserDetails(userMap)
            _isSuccess.value = result
            _isError.value = if (result) null else "Failed to save details"
            _isLoading.value = false
        }
    }

    fun fetchUserProfile() {
        firebaseUser?.uid?.let { userId ->
            db.collection("users").document(userId)
                .get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        val data = document.data?.mapValues { it.value.toString() } ?: emptyMap()
                        _userProfile.value = data
                    }
                }
                .addOnFailureListener {
                    _userProfile.value = emptyMap() // Return an empty map on failure
                }
        }
    }

    fun fetchUserDetails(context: Context, onSuccess: (String, String, String, String, String) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            _isLoading.value = true
            try {
                val userDetails = repository.fetchUserDetails() // Fetch data from Firestore or local storage
                _isLoading.value = false
                if (userDetails.isNotEmpty()) {
                    val name = userDetails["name"] ?: ""
                    val shopName = userDetails["shopName"] ?: ""
                    val phoneNumber = userDetails["phoneNumber"] ?: ""
                    val address = userDetails["address"] ?: ""
                    val email = userDetails["email"] ?: ""

                    onSuccess(name, shopName, phoneNumber, address, email)
                } else {
                    _isError.value = "No user details found"
                }
            } catch (e: Exception) {
                _isLoading.value = false
                _isError.value = "Error fetching user details: ${e.localizedMessage}"
            }
        }
    }


    fun updateProfile(
        name: String,
        shopName: String,
        phoneNumber: String,
        address: String,
        onSuccess: () -> Unit
    ) {
        val userMap = mapOf(
            "name" to name,
            "shopName" to shopName,
            "phoneNumber" to phoneNumber,
            "address" to address
        )

        viewModelScope.launch(Dispatchers.IO) {
            _isLoading.value = true
            val result = repository.updateProfile(userMap)
            _isSuccess.value = result
            _isError.value = if (result) null else "Failed to update profile"
            _isLoading.value = false
            if (result) onSuccess()
        }
    }
}
