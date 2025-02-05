package com.example.mybulkyee.domain.repository

import com.example.mybulkyee.data.CartItem
import com.example.mybulkyee.data.Item
import javax.inject.Inject

class CartRepository  @Inject constructor(){

    fun getCartItems(selectedItems: Map<String, Int>, items: List<Item>): List<CartItem> {
        return selectedItems.mapNotNull { (itemId, quantity) ->
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

    fun getTotalCost(selectedItems: Map<String, Int>, items: List<Item>): Double {
        return selectedItems.mapNotNull { (itemId, quantity) ->
            items.find { it.itemId == itemId }?.discountedPrice?.times(quantity)
        }.sumOf { it.toDouble() }
    }
}
