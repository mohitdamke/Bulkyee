package com.example.bulkyee.data

data class Order(
    val orderId: String = "",
    val orderDate: String = "",
    val orderItems: List<String> = emptyList(),
    val status: String = ""
)
