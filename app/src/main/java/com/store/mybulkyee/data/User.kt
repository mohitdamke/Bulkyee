package com.store.mybulkyee.data

data class User(
    val uid: String = "",
    val email: String = "",
    val name: String = "",
    val phone: String = "",
    val address: String = "",
    val shopName: String = "",
    val createdAt: Long = System.currentTimeMillis()
)
