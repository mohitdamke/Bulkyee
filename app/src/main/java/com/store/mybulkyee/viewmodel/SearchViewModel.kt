package com.store.mybulkyee.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.store.mybulkyee.data.Item
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

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
                val result = db.collection("items")
                    .whereGreaterThanOrEqualTo("itemName", query)
                    .whereLessThanOrEqualTo("itemName", query + "\uf8ff") // Firestore text search trick
                    .get()
                    .await()

                val itemList = result.documents.mapNotNull { it.toObject(Item::class.java) }

                withContext(Dispatchers.Main) {
                    _searchResults.value = itemList
                    _errorMessage.value = if (itemList.isEmpty()) "No products found" else null
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    _errorMessage.value = "Failed to load products: ${e.message}"
                }
            } finally {
                withContext(Dispatchers.Main) {
                    _isLoading.value = false
                }
            }
        }
    }
}
