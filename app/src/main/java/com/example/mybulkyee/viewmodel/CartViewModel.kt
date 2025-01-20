package com.example.mybulkyee.viewmodel

import androidx.lifecycle.ViewModel
import com.example.mybulkyee.data.CartItem
import com.example.mybulkyee.data.Item
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class CartViewModel : ViewModel() {

    private val _selectedItems = MutableStateFlow<Map<String, Int>>(emptyMap())
    val selectedItems: StateFlow<Map<String, Int>> = _selectedItems

    fun updateItemQuantity(itemId: String, quantity: Int) {
        _selectedItems.value = _selectedItems.value.toMutableMap().apply {
            this[itemId] = quantity
        }
    }

    fun getCartItems(items: List<Item>): List<CartItem> {
        return _selectedItems.value.mapNotNull { (itemId, quantity) ->
            items.find { it.itemId == itemId }?.let {
                CartItem(
                    itemId = it.itemId,
                    itemName = it.itemName,
                    quantity = quantity,
                    discountedPrice = it.discountedPrice,
                    realPrice = it.realPrice
                )
            }
        }
    }

    fun getTotalCost(items: List<Item>): Double {
        return _selectedItems.value.mapNotNull { (itemId, quantity) ->
            items.find { it.itemId == itemId }?.discountedPrice?.times(quantity)
        }.sumOf { it.toDouble() }
    }



}