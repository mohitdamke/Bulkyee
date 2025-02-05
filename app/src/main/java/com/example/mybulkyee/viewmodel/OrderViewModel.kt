package com.example.mybulkyee.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.example.mybulkyee.data.Order
import com.example.mybulkyee.domain.repository.OrderRepository
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
        repository.getCurrentUserId()?.let { fetchOrders(it) }
    }


    fun placeOrder(
        context: Context,
        cartQueryParam: String?,
        totalPrice: Int?,
        navController: NavController
    ) {
        viewModelScope.launch {
            repository.placeOrder(context, cartQueryParam, totalPrice, navController)
        }
    }


    fun fetchOrders(userId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            _isLoading.value = true  // Start loading
            _orders.value = repository.fetchOrders(userId)  // Fetch orders from repository
            _isLoading.value = false  // End loading
        }
    }

}
