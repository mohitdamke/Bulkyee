package com.example.bulkyee.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.bulkyee.data.Item
import com.example.bulkyee.navigation.Routes

@Composable
fun CheckOutScreen(cartQueryParam: String?, navController: NavController) {

    val cartItems = cartQueryParam?.split(",")?.mapNotNull { cartEntry ->
        try {
            val parts = cartEntry.split(":")
            Item(
                itemId = parts[0],
                quantity = parts[1].toInt(),
                itemName = parts[2],
                discountedPrice = parts[3].toInt(),
                realPrice = parts[4].toInt(),
                imageUrl = "" // Add if available
            )
        } catch (e: Exception) {
            null // Handle invalid data gracefully
        }
    } ?: emptyList()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Checkout",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        LazyColumn(modifier = Modifier.weight(1f)) {
            items(cartItems) { item ->
                Text(text = "${item.itemName} x ${item.quantity}")
                Text(text = "Discounted Price: ₹${item.discountedPrice}")
                Text(text = "Real Price: ₹${item.realPrice}")
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        val totalPrice = cartItems.sumOf { it.discountedPrice * it.quantity }
        Text(text = "Total: ₹$totalPrice", fontSize = 20.sp, fontWeight = FontWeight.Bold)
        Button(
            onClick = {
                // Encode the cart data
                val encodedCartData = cartItems.joinToString(",") {
                    "${it.itemId}:${it.quantity}:${it.itemName}:${it.discountedPrice}:${it.realPrice}"
                }
                // Create the route by replacing placeholders with actual values
                val route = Routes.AddressAndPaymentScreen.routes

                navController.navigate(
                    route.replace(
                        oldValue = "{cartQueryParam}",
                        newValue = cartQueryParam!!
                    )
                )
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Place Order")
        }
    }
}
