package com.store.mybulkyee.domain.repository

import android.content.Context
import android.util.Log
import android.widget.Toast
import com.store.mybulkyee.data.Item
import com.store.mybulkyee.data.Order
import com.store.mybulkyee.data.OrderItem
import com.store.mybulkyee.data.PreferencesHelper
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class OrderRepository @Inject constructor(
    private val db: FirebaseFirestore,
    private val auth: FirebaseAuth
) {

    suspend fun placeOrder(
        context: Context,
        cartQueryParam: String?,
        totalPrice: Int?
    ): Boolean {
        if (cartQueryParam.isNullOrBlank() || totalPrice == null || totalPrice <= 0) {
            Toast.makeText(context, "Invalid order details", Toast.LENGTH_SHORT).show()
            return false
        }

        val userId = auth.currentUser?.uid ?: return false

        val userInfo = PreferencesHelper.getUserInfo(context)
        val userName = userInfo["name"] ?: "No Name Found"
        val userEmail = userInfo["email"] ?: "No Email Found"
        val phoneNumber = userInfo["phoneNumber"] ?: "No Phone Number Found"
        val shopName = userInfo["shopName"] ?: "No Shop Name Found"
        val userAddress = userInfo["address"] ?: "No Address Found"

        val cartItems = parseCartItems(cartQueryParam)

        if (cartItems.isEmpty()) {
            Toast.makeText(context, "Cart is empty.", Toast.LENGTH_SHORT).show()
            return false
        }

        // Convert List<Item> to List<OrderItem>
        val orderItems = cartItems.map { item ->
            OrderItem(
                itemId = item.itemId,
                itemName = item.itemName,
                quantity = item.quantity,
                discountedPrice = item.discountedPrice,
                realPrice = item.realPrice
            )
        }
        val orderId = db.collection("user_orders").document().id

        val order = Order(
            orderId = orderId,
            userId = userId,
            userName = userName,
            userEmail = userEmail,
            phoneNumber = phoneNumber,
            shopName = shopName,
            userAddress = userAddress,
            items = orderItems,
            totalPrice = totalPrice,
            status = "Pending",
            timestamp = System.currentTimeMillis()
        )

        return try {
            db.runBatch { batch ->
                batch.set(db.collection("user_orders").document(orderId), order)
                batch.set(db.collection("admin_orders").document(orderId), order)
            }.await()

            Log.d("OrderRepository", "Order placed successfully.")
            true
        } catch (e: Exception) {
            Log.e("OrderRepository", "Error placing order: ${e.message}")
            false
        }
    }

    suspend fun fetchOrders(userId: String): List<Order> {
        return try {
            Log.d("OrderRepository", "Fetching orders for user: $userId")
            val result = db.collection("user_orders")
                .whereEqualTo("userId", userId)
                .get()
                .await()

            if (result.isEmpty) {
                Log.d("OrderRepository", "No orders found.")
                emptyList()
            } else {
                val orderList = result.documents.mapNotNull { it.toObject(Order::class.java) }
                Log.d("OrderRepository", "Orders found: ${orderList.size}")
                orderList
            }
        } catch (e: Exception) {
            Log.e("OrderRepository", "Error fetching orders: ${e.message}")
            emptyList()
        }
    }

    private fun parseCartItems(cartQueryParam: String): List<Item> {
        return cartQueryParam.split(",").mapNotNull { cartEntry ->
            try {
                val parts = cartEntry.split(":")
                Item(
                    itemId = parts[0],
                    quantity = parts[1].toInt(),
                    itemName = parts[2],
                    discountedPrice = parts[3].toInt(),
                    realPrice = parts[4].toInt(),
                    imageUrl = ""
                )
            } catch (e: Exception) {
                Log.e("OrderRepository", "Error parsing cart item: $cartEntry", e)
                null
            }
        }
    }


    fun getCurrentUserId(): String? {
        return auth.currentUser?.uid
    }
}
