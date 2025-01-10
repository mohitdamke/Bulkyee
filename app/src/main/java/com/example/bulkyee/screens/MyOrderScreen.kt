package com.example.bulkyee.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.bulkyee.data.Order
import com.example.bulkyee.viewmodel.OrderViewModel
import com.google.firebase.auth.FirebaseAuth

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyOrderScreen(modifier: Modifier = Modifier, navController: NavController) {

    val context = LocalContext.current
    val orderViewModel: OrderViewModel = viewModel()
    val userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
    val orders by orderViewModel.orders.collectAsState()
    val isLoading by orderViewModel.isLoading.collectAsState()
    val isError by orderViewModel.isError.collectAsState()
    val isSuccess by orderViewModel.isSuccess.collectAsState()

    // Fetch orders for the user
    LaunchedEffect(userId) {
        if (userId.isNotEmpty()) {
            orderViewModel.fetchOrders(userId)
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text(text = "My Orders") }
            )
        }
    ) { paddingValues ->
        if (isLoading) {
            // Show loading indicator
            Box(modifier = modifier.fillMaxSize()) {
                CircularProgressIndicator(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .size(50.dp)
                )
            }
        } else if (isError) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "Failed to load orders.", color = Color.Red)
            }
        } else if (orders.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "You have no orders yet.", fontSize = 18.sp)
            }
        } else {
            LazyColumn(modifier = Modifier.padding(paddingValues)) {
                items(orders.size) { index ->
                    val order = orders[index]
                    OrderItemView(order = order)
                }
            }
        }
    }
}

@Composable
fun OrderItemView(order: Order) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(text = "Order ID: ${order.orderId}", fontSize = 16.sp)
        Text(text = "Date: ${order.orderDate}", fontSize = 14.sp)
        Text(text = "Status: ${order.status}", fontSize = 14.sp)
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = "Items: ${order.orderItems.joinToString(", ")}", fontSize = 14.sp)
        Spacer(modifier = Modifier.height(16.dp))
    }
}
