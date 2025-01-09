package com.example.bulkyee.viewmodel

import android.app.Activity
import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bulkyee.R
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.UUID
import javax.inject.Inject

class LoginViewModel @Inject constructor() : ViewModel() {

    private lateinit var googleSignInClient: GoogleSignInClient
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()
    private val _isUserLoggedIn = MutableLiveData<Boolean>()
    val isUserLoggedIn: LiveData<Boolean> get() = _isUserLoggedIn

    // Loading state for data fetch operations
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    init {
        _isUserLoggedIn.value = auth.currentUser != null
        auth.addAuthStateListener { firebaseAuth ->
            _isUserLoggedIn.value = firebaseAuth.currentUser != null
        }
    }


    // Google Sign-In client configuration
    fun getGoogleSignInClient(activity: Activity): GoogleSignInClient {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(activity.getString(R.string.default_web_client_id))
            .requestEmail()
            .build()


        googleSignInClient = GoogleSignIn.getClient(activity, gso)

        // Force account chooser even if the user has already signed in
        googleSignInClient.signOut()

        return googleSignInClient
    }

    // Firebase Authentication with Google
    fun firebaseAuthWithGoogle(account: GoogleSignInAccount?, onResult: (Boolean) -> Unit) {
        account?.let {
            val credential: AuthCredential = GoogleAuthProvider.getCredential(account.idToken, null)
            _isLoading.postValue(true)

            viewModelScope.launch {
                try {
                    val authResult = auth.signInWithCredential(credential).await()
                    val user = authResult.user

                    user?.let {
                        // Fallback to GoogleSignInAccount for email and display name
                        val email = account.email ?: it.email   // Get email from GoogleSignInAccount first, fallback to FirebaseUser
                        val displayName = account.displayName ?: it.displayName  // Get name from GoogleSignInAccount first

                        // Ensure email is not null
                        if (email != null) {
                            // Store user info in Firestore
                            val userData = hashMapOf(
                                "email" to email,
                                "name" to displayName.orEmpty(),
                                "phone" to "",
                                "address" to "",
                                "shopName" to "",
                                "uid" to it.uid,
                                "createdAt" to System.currentTimeMillis()
                            )

                            // Save the user data to Firestore
                            firestore.collection("users").document(it.uid)
                                .set(userData)
                                .addOnSuccessListener {
                                    Log.d("Firestore", "User data stored successfully")
                                    onResult(true)
                                }
                                .addOnFailureListener { e ->
                                    Log.e("Firestore", "Error storing user data: ${e.message}")
                                    onResult(false)
                                }
                        } else {
                            Log.e("Firestore", "Error: Email is null")
                            onResult(false)
                        }
                    } ?: run {
                        onResult(false)
                    }
                } catch (e: Exception) {
                    Log.e("FirebaseAuth", "Google sign-in failed: ${e.message}")
                    onResult(false)
                } finally {
                    _isLoading.postValue(false)
                }
            }
        } ?: run {
            onResult(false)
        }
    }

    // Check if user is already logged in
    fun isUserLoggedIn(): Boolean {
        return auth.currentUser != null
    }

    fun showErrorMessage(context: Context, message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    // Sign out
    fun signOut() {
        viewModelScope.launch {
            _isLoading.postValue(true)

            if (::googleSignInClient.isInitialized) {
                googleSignInClient.signOut()
            }
            auth.signOut()
            _isLoading.postValue(false)

        }
    }

    // Update user details
    fun updateUserDetails(
        userId: String,
        name: String = "",
        phone: String = "",
        address: String = "",
        shopName: String = ""
    ) {
        val updates = mutableMapOf<String, Any>()
        if (name.isNotEmpty()) updates["name"] = name
        if (phone.isNotEmpty()) updates["phone"] = phone
        if (address.isNotEmpty()) updates["address"] = address
        if (shopName.isNotEmpty()) updates["shopName"] = shopName

        firestore.collection("users").document(userId)
            .update(updates)
            .addOnSuccessListener {
                Log.d("Firestore", "User details updated successfully.")
            }
            .addOnFailureListener { e ->
                Log.e("Firestore", "Error updating user details: ${e.message}")
            }
    }

    // Function to save the cart and order data

//    fun saveCartToFirestore(userId: String, cart: List<Product>, status: String = "pending") {
//        val db = FirebaseFirestore.getInstance()
//
//        // Create the list of products in the cart
//        val cartItems = cart.map {
//            mapOf(
//                "productId" to it.id,
//                "name" to it.name,
//                "price" to it.price,
//                "quantity" to it.quantity
//            )
//        }
//
//        // Generate a custom order ID using UUID
//        val orderId = UUID.randomUUID().toString()
//
//        val orderMap = mapOf(
//            "orderId" to orderId,
//            "userId" to userId,
//            "cartItems" to cartItems,
//            "totalAmount" to cart.sumOf { it.price * it.quantity },  // Total amount of the cart
//            "orderDate" to System.currentTimeMillis(),
//            "status" to status,  // Adding the order status field (pending or success)
//        )
//
//        // Save the order data to Firestore under the user's orders collection
//        db.collection("users").document(userId)
//            .collection("orders")  // Store orders as sub-collections
//            .document(orderId)  // Save using the custom order ID
//            .set(orderMap)
//            .addOnSuccessListener {
//                Log.d("Firestore", "Order saved successfully with Order ID: $orderId")
//            }
//            .addOnFailureListener { e ->
//                Log.e("Firestore", "Error saving order: ${e.message}")
//            }
//    }


}