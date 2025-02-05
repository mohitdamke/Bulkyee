package com.example.mybulkyee.viewmodel

import androidx.lifecycle.ViewModel
import com.example.mybulkyee.data.CartItem
import com.example.mybulkyee.data.Item
import com.example.mybulkyee.domain.repository.CartRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class CartViewModel @Inject constructor(private val repository: CartRepository) : ViewModel() {

    private val _selectedItems = MutableStateFlow<Map<String, Int>>(emptyMap())
    val selectedItems: StateFlow<Map<String, Int>> = _selectedItems

    fun updateItemQuantity(itemId: String, quantity: Int) {
        _selectedItems.value = _selectedItems.value.toMutableMap().apply {
            this[itemId] = quantity
        }
    }

    fun getCartItems(items: List<Item>): List<CartItem> {
        return repository.getCartItems(_selectedItems.value, items)
    }

    fun getTotalCost(items: List<Item>): Double {
        return repository.getTotalCost(_selectedItems.value, items)
    }
}
