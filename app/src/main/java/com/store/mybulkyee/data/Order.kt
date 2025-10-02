package com.store.mybulkyee.data


data class Order(
    val orderId: String = "",          // Order ID
    val userName: String = "",         // User Name
    val shopName: String = "",         // Shop Name
    val phoneNumber: String = "",      // Phone Number
    val status: String = "",           // Order Status
    val userAddress: String = "",      // User Address
    val userEmail: String = "",        // User Email
    val userId: String = "",           // User ID
    val items: List<OrderItem> = listOf(), // List of Order Items
    val totalPrice: Int = 0,           // Total Price
    val timestamp: Long = 0L,           // Timestamp
)

data class OrderItem(
    val itemId: String = "",           // Item ID
    val itemName: String = "",         // Item Name
    val quantity: Int = 0,             // Item Quantity
    val discountedPrice: Int = 0,      // Discounted Price
    val realPrice: Int = 0             // Real Price
)

