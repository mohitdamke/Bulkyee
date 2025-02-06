package com.example.mybulkyee.domain.repository

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.navigation.NavController
import com.example.mybulkyee.data.Order
import com.example.mybulkyee.navigation.Routes
import com.example.mybulkyee.screens.showOrderNotification
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class OrderRepository @Inject constructor(private val db: FirebaseFirestore, private val auth: FirebaseAuth) {

    suspend fun placeOrder(
        context: Context,
        cartQueryParam: String?,
        totalPrice: Int?,
        navController: NavController
    ) {
        if (cartQueryParam.isNullOrBlank() || totalPrice == null || totalPrice <= 0) {
            Toast.makeText(context, "Invalid order details", Toast.LENGTH_SHORT).show()
            return
        }

        val userId = auth.currentUser?.uid
        if (userId.isNullOrBlank()) {
            Toast.makeText(context, "User not logged in", Toast.LENGTH_SHORT).show()
            return
        }

        val orderId = db.collection("user_orders").document().id  // Generate Firestore ID
        val orderData = hashMapOf(
            "orderId" to orderId,
            "userId" to userId,
            "cartQueryParam" to cartQueryParam,
            "totalPrice" to totalPrice,
            "timestamp" to System.currentTimeMillis()
        )

        try {
            db.collection("user_orders").document(orderId).set(orderData).await()
            showOrderNotification(context)
            Toast.makeText(context, "Order placed successfully!", Toast.LENGTH_SHORT).show()

            // Navigate to Order Confirmation screen
            navController.navigate(Routes.HomeScreen.routes) {
                popUpTo(Routes.CheckOutScreen.routes) { inclusive = true }
            }
        } catch (e: Exception) {
            Log.e("OrderRepository", "Error placing order: ${e.message}")
            Toast.makeText(context, "Failed to place order. Try again.", Toast.LENGTH_SHORT).show()
        }
    }


    suspend fun fetchOrders(userId: String): List<Order> {
        return try {
            val result = db.collection("user_orders")
                .whereEqualTo("userId", userId)
                .get()
                .await()

            result.documents.mapNotNull { it.toObject(Order::class.java) }
        } catch (e: Exception) {
            Log.e("OrderRepository", "Error fetching orders: ${e.message}")
            emptyList()
        }
    }

    fun getCurrentUserId(): String? {
        return auth.currentUser?.uid
    }
}
