package com.example.bulkyee.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bulkyee.data.Item
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class SearchViewModel : ViewModel() {

    private val db = FirebaseFirestore.getInstance()

    private val _searchResults = MutableStateFlow<List<Item>>(emptyList())
    val searchResults: StateFlow<List<Item>> = _searchResults

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    // Search products based on query
    fun searchProducts(query: String) {
        if (query.isBlank()) {
            _searchResults.value = emptyList() // Clear results if query is empty
            return
        }

        _isLoading.value = true
        viewModelScope.launch(Dispatchers.IO) {
            try {
                // Lowercase the query for case-insensitive comparison
                val lowerCaseQuery = query.lowercase()

                val result = db.collection("items")
                    .get()
                    .await()

                // Filter the results by comparing lowercase item names
                val itemList = result.filter { document ->
                    val itemName = document.getString("itemName")?.lowercase() ?: ""
                    itemName.contains(lowerCaseQuery)
                }.map { it.toObject(Item::class.java) }

                if (itemList.isEmpty()) {
                    _searchResults.value = emptyList()
                } else {
                    _searchResults.value = itemList
                }
            } catch (e: Exception) {
                _errorMessage.value = "Failed to load products: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
}
