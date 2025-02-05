package com.example.mybulkyee.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.mybulkyee.data.Order
import com.example.mybulkyee.dimensions.FamilyDim
import com.example.mybulkyee.dimensions.FontDim
import com.example.mybulkyee.navigation.Routes
import com.example.mybulkyee.ui.theme.Brown40
import com.example.mybulkyee.ui.theme.White10
import com.example.mybulkyee.viewmodel.OrderViewModel
import com.google.firebase.auth.FirebaseAuth
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyOrderScreen(modifier: Modifier = Modifier, navController: NavController) {

    val context = LocalContext.current
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())
    val orderViewModel: OrderViewModel = hiltViewModel()
    val userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
    val orders by orderViewModel.orders.collectAsState()
    val isLoading by orderViewModel.isLoading.collectAsState()
    val isError by orderViewModel.isError.collectAsState()

    // Fetch orders for the user
    LaunchedEffect(userId) {
        if (userId.isNotEmpty()) {
            orderViewModel.fetchOrders(userId)
        }
    }

    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .background(White10)
            .nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            MyOrderTopAppBar(navController = navController, scrollBehavior = scrollBehavior)
        },
    ) { paddingValues ->
        when {
            isLoading -> {
                // Show loading indicator
                Box(modifier = modifier.fillMaxSize().background(White10)) {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .size(50.dp)
                    )
                }
            }

            isError -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(White10)
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "Failed to load orders.", color = Color.Red, fontSize = 18.sp)
                }
            }

            orders.isEmpty() -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(White10)
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "You have no orders yet.", fontSize = 18.sp, color = Color.Gray)
                }
            }

            else -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize()
                        .background(White10)
                        .padding(paddingValues)
                ) {
                    items(orders.size) { index ->
                        val order = orders[index]
                        OrderItemView(order = order)
                    }
                }
            }
        }
    }
}

@Composable
fun OrderItemView(order: Order) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        colors = CardDefaults.cardColors(Color.White),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.background(White10).padding(16.dp)) {
            Text(
                text = "Order ID: ${order.orderId}",
                fontSize = FontDim.mediumTextSize,
                fontFamily = FamilyDim.Medium, color = Color.Black,
            )
            Spacer(modifier = Modifier.height(8.dp))

            StatusText(status = order.status)
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Date: ${formatDate(order.timestamp)}",
                fontSize = FontDim.mediumTextSize,
                fontFamily = FamilyDim.Medium, color = Color.Black,
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Items: ",
                    fontSize = FontDim.mediumTextSize,
                    fontFamily = FamilyDim.Medium, color = Color.Black,
                )
                Text(
                    text = order.items.joinToString(", ") { it.itemName }.replace("+", " "),
                    fontSize = FontDim.mediumTextSize,
                    fontFamily = FamilyDim.Medium, color = Color.Black,
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Total Price: ₹${order.totalPrice}",
                fontSize = FontDim.mediumTextSize,
                fontFamily = FamilyDim.Medium,
                color = Color.Black
            )
        }
    }
}

@Composable
fun StatusText(status: String) {
    val color = when (status) {
        "Success" -> Color.Green
        "Pending" -> Color.Red
        else -> Color.Gray
    }

    Text(
        text = "Status: $status",
        fontSize = FontDim.mediumTextSize,
        fontFamily = FamilyDim.Medium, color = color,
    )
}

fun formatDate(timestamp: Long): String {
    val date = Date(timestamp)
    val format = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
    return format.format(date)
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyOrderTopAppBar(
    navController: NavController,
    scrollBehavior: TopAppBarScrollBehavior
) {
    CenterAlignedTopAppBar(
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = White10,
            navigationIconContentColor = Brown40
        ),
        title = {
            Text(
                text = "My Orders",
                fontSize = FontDim.extraLargeTextSize,
                fontFamily = FamilyDim.Bold,
                color = Brown40
            )
        },
        scrollBehavior = scrollBehavior,
        navigationIcon = {
            Icon(
                imageVector = Icons.Rounded.ArrowBack,
                contentDescription = "Go Back",
                modifier = Modifier
                    .size(30.dp)
                    .clickable { navController.navigate(Routes.HomeScreen.routes) }
            )
        }
    )
}