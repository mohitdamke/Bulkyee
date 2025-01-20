package com.example.mybulkyee.data

data class CartItem(
    var itemId: String = "",
    val itemName: String = "",
    val itemDesc: String = "",
    val realPrice: Int = 0, // Changed to Int
    val discountedPrice: Int = 0, // Changed to Int
    val imageUrl: String = "",
    val quantity: Int = 0
)
