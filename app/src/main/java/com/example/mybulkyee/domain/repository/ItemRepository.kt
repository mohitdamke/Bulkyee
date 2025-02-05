package com.example.mybulkyee.domain.repository

import com.example.mybulkyee.data.Item
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class ItemRepository  @Inject constructor(){
    private val db = FirebaseFirestore.getInstance()

    suspend fun fetchItems(): List<Item> {
        return try {
            val result = db.collection("items").get().await()
            result.map { it.toObject(Item::class.java) }
        } catch (e: Exception) {
            emptyList()
        }
    }
}
