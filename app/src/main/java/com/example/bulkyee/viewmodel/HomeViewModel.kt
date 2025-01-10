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

class HomeViewModel : ViewModel() {

    private val db = FirebaseFirestore.getInstance()

    private val _items = MutableStateFlow<List<Item>>(emptyList())
    val items: StateFlow<List<Item>> = _items

    private val _isLoading = MutableStateFlow<Boolean>(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _isSuccess = MutableStateFlow<Boolean>(false)
    val isSuccess: StateFlow<Boolean> = _isSuccess

    private val _isError = MutableStateFlow<Boolean>(false)
    val isError: StateFlow<Boolean> = _isError

    init {
        fetchItems()
    }

    fun fetchItems() {
        viewModelScope.launch(Dispatchers.IO) {
            _isLoading.value = true
            _isError.value = false
            try {
                val result = db.collection("items").get().await()
                if (result.isEmpty) {
                    _isError.value = false
                    _isSuccess.value = true

                } else {
                    val itemList = result.map { it.toObject(Item::class.java) }
                    _items.value = itemList
                    _isSuccess.value = true
                }
            } catch (e: Exception) {
                _isError.value = true
            } finally {
                _isLoading.value = false
            }
        }
    }


}