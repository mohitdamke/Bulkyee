package com.store.mybulkyee.viewmodel

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.store.mybulkyee.data.Order
import com.store.mybulkyee.domain.repository.OrderRepository
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OrderViewModel @Inject constructor(private val repository: OrderRepository) : ViewModel() {

    private val _orders = MutableStateFlow<List<Order>>(emptyList())
    val orders: StateFlow<List<Order>> = _orders

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _isSuccess = MutableStateFlow(false)
    val isSuccess: StateFlow<Boolean> = _isSuccess

    private val _isError = MutableStateFlow(false)
    val isError: StateFlow<Boolean> = _isError

    init {
        val currentUserId = FirebaseAuth.getInstance().currentUser?.uid
        if (!currentUserId.isNullOrEmpty()) {
            fetchOrders(userId = currentUserId)
        }
    }

    fun placeOrder(
        context: Context,
        cartQueryParam: String?,
        totalPrice: Int?,
        navController: NavController
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            _isLoading.value = true
            val success = repository.placeOrder(context, cartQueryParam, totalPrice)
            _isLoading.value = false

            if (success) {
                _isSuccess.value = true
                Log.d("OrderViewModel", "Order placed successfully!")

                // Check if the navigation is proceeding properly
                Log.d("OrderViewModel", "Navigating to HomeScreen")

            } else {
                _isError.value = true
                Log.e("OrderViewModel", "Failed to place order")
                Toast.makeText(context, "Failed to place order. Try again.", Toast.LENGTH_SHORT).show()
            }
        }
    }


    fun fetchOrders(userId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            _isLoading.value = true
            _isError.value = false

            val result = repository.fetchOrders(userId)

            if (result.isNotEmpty()) {
                _orders.value = result
            } else {
                _isError.value = true
            }

            _isLoading.value = false
        }
    }


}
