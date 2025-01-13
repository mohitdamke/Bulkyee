package com.example.bulkyee.screens

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.bulkyee.data.Item
import com.example.bulkyee.data.PreferencesHelper
import com.example.bulkyee.dimensions.FamilyDim
import com.example.bulkyee.dimensions.FontDim
import com.example.bulkyee.navigation.Routes
import com.example.bulkyee.ui.theme.Brown40
import com.example.bulkyee.ui.theme.White10
import com.example.bulkyee.viewmodel.OrderViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckOutScreen(
    modifier: Modifier = Modifier,
    cartQueryParam: String?,
    navController: NavController,
) {

    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())
    val context = LocalContext.current
    val orderViewModel: OrderViewModel = viewModel()
    val isLoading by orderViewModel.isLoading.collectAsState(false)
    val scope = rememberCoroutineScope()
    val isSuccess by orderViewModel.isSuccess.collectAsState(false)
    val isError by orderViewModel.isError.collectAsState(false)
    val userInfo = PreferencesHelper.getUserInfo(context)
    val name = userInfo["name"]
    val shopName = userInfo["shopName"]
    val phoneNumber = userInfo["phoneNumber"]
    val address = userInfo["address"]
    val email = userInfo["email"]


    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .background(White10)
            .nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            CheckoutTopAppBar(navController = navController, scrollBehavior = scrollBehavior)
        },
    ) { paddingValues ->
        val cartItems = parseCartItems(cartQueryParam)

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(White10)
                .padding(paddingValues)
                .padding(10.dp)
        ) {
            if (isLoading) {
                Box(modifier = modifier.fillMaxSize()) {
                    // Show loading indicator
                    CircularProgressIndicator(
                        modifier = Modifier
                            .background(White10)
                            .align(Alignment.Center)
                            .size(50.dp)
                    ) // Adjust the size to prevent it from taking full size)
                }
            }
            if (cartItems.isEmpty()) {
                Text(
                    text = "Your cart is empty.",
                    fontSize = FontDim.largeTextSize,
                    fontFamily = FamilyDim.SemiBold,
                    color = Color.Gray,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxSize()
                )
                return@Scaffold // Exit early if cart is empty

            } else {
                DeliveryAddressSection(
                    name = "$name",
                    shopName = "$shopName",
                    address = "$address",
                    email = "$email",
                    phoneNumber = "$phoneNumber"
                )
                Text(
                    "Items:", fontFamily = FamilyDim.Bold,
                    fontSize = FontDim.largeTextSize,
                )

                if (cartQueryParam.isNullOrEmpty()) {
                    Text("No items in the cart", modifier = Modifier.padding(8.dp))
                } else
                    LazyColumn(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth(),
                        contentPadding = PaddingValues(vertical = 8.dp)
                    ) {
                        items(cartItems) { item ->
                            CartItem(item = item)
                        }
                    }

                Spacer(modifier = Modifier.height(16.dp))

                val totalPrice = cartItems.sumOf { it.discountedPrice * it.quantity }
                val formattedPrice =
                    "₹${totalPrice.toString().reversed().chunked(3).joinToString(",").reversed()}"
                Text(
                    text = "Total: $formattedPrice",
                    fontSize = FontDim.largeTextSize,
                    fontFamily = FamilyDim.SemiBold,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                Button(
                    onClick = {
                        Log.d("CheckOutScreen", "Place Order button clicked")
                        if (cartItems.isNotEmpty()) {
                            orderViewModel.placeOrder(
                                context = context,
                                cartQueryParam = cartQueryParam,
                                navController = navController,
                                totalPrice = totalPrice // Pass totalPrice here
                            )
                            navController.navigate(Routes.HomeScreen.routes) {
                                popUpTo(navController.graph.startDestinationId) { inclusive = true } // This will clear the back stack and ensure no back navigation
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp),
                    colors = ButtonDefaults.buttonColors(Brown40),
                    enabled = cartItems.isNotEmpty()
                ) {
                    Text(
                        text = "Place Order", fontSize = FontDim.largeTextSize,
                        fontFamily = FamilyDim.SemiBold,
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckoutTopAppBar(
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
                text = "Checkout",
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

@Composable
fun CartItem(modifier: Modifier = Modifier, item: Item) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(10.dp),
        colors = CardDefaults.cardColors(Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp)
        ) {
            Text(
                text = "${item.itemName} x ${item.quantity}", fontSize = FontDim.mediumTextSize,
                fontFamily = FamilyDim.Normal,
            )
            Text(
                text = "Real Price: ₹${item.realPrice}", fontSize = FontDim.mediumTextSize,
                fontFamily = FamilyDim.Normal, textDecoration = TextDecoration.LineThrough
            )
            Text(
                text = "Discounted Price: ₹${item.discountedPrice}",
                fontSize = FontDim.mediumTextSize,
                fontFamily = FamilyDim.Normal,
            )
        }
    }
}


@Composable
fun DeliveryAddressSection(
    name: String,
    shopName: String,
    address: String,
    email: String,
    phoneNumber: String
) {
    Column(modifier = Modifier.padding(10.dp)) {
        Text(
            text = "Delivery Address:",
            fontFamily = FamilyDim.SemiBold,
            fontSize = FontDim.largeTextSize
        )
        Text(
            text = address,
            fontFamily = FamilyDim.Medium,
            fontSize = FontDim.mediumTextSize
        )
        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = "Name:",
            fontFamily = FamilyDim.SemiBold,
            fontSize = FontDim.largeTextSize
        )
        Text(
            text = name,
            fontFamily = FamilyDim.Medium,
            fontSize = FontDim.mediumTextSize,
            modifier = Modifier.padding(vertical = 4.dp)
        )
        Spacer(modifier = Modifier.height(10.dp))


        Text(
            text = "Shop Name:",
            fontFamily = FamilyDim.SemiBold,
            fontSize = FontDim.largeTextSize
        )
        Text(
            text = shopName,
            fontFamily = FamilyDim.Medium,
            fontSize = FontDim.mediumTextSize,
            modifier = Modifier.padding(vertical = 4.dp)
        )
        Spacer(modifier = Modifier.height(10.dp))


        Text(
            text = "Email:",
            fontFamily = FamilyDim.SemiBold,
            fontSize = FontDim.largeTextSize
        )
        Text(
            text = email,
            fontFamily = FamilyDim.Medium,
            fontSize = FontDim.mediumTextSize,
            modifier = Modifier.padding(vertical = 4.dp)
        )

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = "Phone No:",
            fontFamily = FamilyDim.SemiBold,
            fontSize = FontDim.largeTextSize
        )
        Text(
            text = phoneNumber,
            fontFamily = FamilyDim.Medium,
            fontSize = FontDim.mediumTextSize,
            modifier = Modifier.padding(vertical = 4.dp)
        )
    }
}


fun parseCartItems(cartQueryParam: String?): List<Item> {
    return cartQueryParam?.split(",")?.mapNotNull { cartEntry ->
        try {
            val parts = cartEntry.split(":")
            Item(
                itemId = parts[0],
                quantity = parts[1].toInt(),
                itemName = parts[2],
                discountedPrice = parts[3].toInt(),
                realPrice = parts[4].toInt(),
                imageUrl = "" // Optional: Add if available
            )
        } catch (e: Exception) {
            Log.e("CheckOutScreen", "Error parsing cart entry: $cartEntry", e)
            null // Ignore invalid data
        }
    } ?: emptyList()
}
