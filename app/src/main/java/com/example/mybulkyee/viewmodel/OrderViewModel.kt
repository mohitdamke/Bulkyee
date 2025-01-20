package com.example.mybulkyee.viewmodel

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.example.mybulkyee.data.Item
import com.example.mybulkyee.data.Order
import com.example.mybulkyee.data.PreferencesHelper
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class OrderViewModel : ViewModel() {

    private val db = FirebaseFirestore.getInstance()

    private val currentUserId = Firebase.auth.currentUser?.uid

    private val _orders = MutableStateFlow<List<Order>>(emptyList())
    val orders: StateFlow<List<Order>> = _orders

    private val _isLoading = MutableStateFlow<Boolean>(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _isSuccess = MutableStateFlow<Boolean>(false)
    val isSuccess: StateFlow<Boolean> = _isSuccess

    private val _isError = MutableStateFlow<Boolean>(false)
    val isError: StateFlow<Boolean> = _isError

    init {
        if (currentUserId != null) {
            fetchOrders(userId = currentUserId)
        }
    }

    // Function to handle order placement
    fun placeOrder(
        context: Context,
        cartQueryParam: String?,
        totalPrice: Int?,
        navController: NavController
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            _isLoading.value = true

            val userInfo = PreferencesHelper.getUserInfo(context)
            val userId = FirebaseAuth.getInstance().currentUser?.uid ?: "" // Get current user's UID
            val userName = userInfo["name"] ?: "No Name Found"
            val userEmail = userInfo["email"] ?: "No Email Found"
            val phoneNumber = userInfo["phoneNumber"] ?: "No Phone Number Found"
            val shopName = userInfo["shopName"] ?: "No Shop Name Found"
            val userAddress = userInfo["address"] ?: "No Address Found"

            // Parse cart items
            val cartItems = cartQueryParam?.split(",")?.mapNotNull { cartEntry ->
                try {
                    val parts = cartEntry.split(":")
                    Item(
                        itemId = parts[0],
                        quantity = parts[1].toInt(),
                        itemName = parts[2],
                        discountedPrice = parts[3].toInt(),
                        realPrice = parts[4].toInt(),
                        imageUrl = "" // Add if available
                    )
                } catch (e: Exception) {
                    Log.e("OrderViewModel", "Error parsing cart item: $cartEntry", e)
                    null
                }
            } ?: emptyList()

            if (cartItems.isEmpty() || totalPrice == null) {
                _isLoading.value = false
                _isError.value = true
                Toast.makeText(
                    context,
                    "Cart is empty or total price is invalid.",
                    Toast.LENGTH_SHORT
                ).show()
                return@launch
            }

            // Prepare the order data
            val orderId = System.currentTimeMillis().toString() // Generate unique order ID
            val orderData = hashMapOf(
                "orderId" to orderId,
                "userId" to userId,
                "userName" to userName,
                "userEmail" to userEmail,
                "phoneNumber" to phoneNumber,
                "shopName" to shopName,
                "userAddress" to userAddress,
                "items" to cartItems.map {
                    mapOf(
                        "itemId" to it.itemId,
                        "itemName" to it.itemName,
                        "quantity" to it.quantity,
                        "discountedPrice" to it.discountedPrice,
                        "realPrice" to it.realPrice
                    )
                },
                "totalPrice" to totalPrice, // Add totalPrice here
                "status" to "Pending", // Initial status of the order
                "timestamp" to System.currentTimeMillis()
            )

            try {
                db.collection("user_orders")
                    .document(orderId)
                    .set(orderData)
                    .await()

                _isSuccess.value = true
                _isLoading.value = false
                Log.d("OrderViewModel", "Order placed successfully.")
            } catch (e: Exception) {
                _isSuccess.value = false
                _isLoading.value = false
                Log.e("OrderViewModel", "Error placing order: ${e.message}")
                Toast.makeText(context, "Failed to place order.", Toast.LENGTH_SHORT).show()
            }
        }
    }


    // Fetch orders for a specific user (passed userId)
    fun fetchOrders(userId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            _isLoading.value = true
            _isError.value = false

            try {
                val result = db.collection("user_orders")
                    .whereEqualTo("userId", userId)
                    .get()
                    .await()

                if (result.isEmpty) {
                    _orders.value = emptyList()
                    _isSuccess.value = true
                } else {
                    val orderList = result.documents.mapNotNull { it.toObject(Order::class.java) }
                    _orders.value = orderList
                    _isSuccess.value = true
                }
            } catch (e: Exception) {
                Log.e("OrderViewModel", "Error fetching orders: ${e.message}")
                _isError.value = true
            } finally {
                _isLoading.value = false
            }
        }
    }
}