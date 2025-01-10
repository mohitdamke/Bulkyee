package com.example.bulkyee.screens

import android.content.Context
import android.util.Log
import android.widget.Toast
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
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.bulkyee.data.Item
import com.example.bulkyee.data.PreferencesHelper
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun AddressAndPaymentScreen(
    navController: NavController,
    cartQueryParam: String? = null,
) {

    val context = LocalContext.current
    val sharedPreferences = context.getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
    val userInfo = PreferencesHelper.getUserInfo(context)
    // Fetch user details from SharedPreferences

    // Retrieve individual values from the map
    val userName = userInfo["name"] ?: "No Name Found"
    val userEmail = userInfo["email"] ?: "No Email Found"
    val phoneNumber = userInfo["phoneNumber"] ?: "No Phone Number Found"
    val shopName = userInfo["shopName"] ?: "No Shop Name Found"
    val userAddress = userInfo["address"] ?: "No Address Found"


    // Parse cart items outside LaunchedEffect, using remember
    val cartItems = remember(cartQueryParam) {
        cartQueryParam?.split(",")?.mapNotNull { cartEntry ->
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
                Log.e("AddressAndPaymentScreen", "Error parsing cart item: $cartEntry", e)
                null
            }
        } ?: emptyList()
    }

    // Logging cart items for debugging
    Log.d("AddressAndPaymentScreen", "Parsed cartItems: $cartItems")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        Text("Delivery Address:", fontWeight = FontWeight.Bold, fontSize = 18.sp)
        Text(userAddress, modifier = Modifier.padding(8.dp))

        Text("Items:", fontWeight = FontWeight.Bold, fontSize = 18.sp)

        if (cartItems.isEmpty()) {
            Text("No items in the cart", modifier = Modifier.padding(8.dp))
        } else {
            LazyColumn(modifier = Modifier.weight(1f)) {
                items(cartItems) { item ->
                    Text(
                        text = "${item.itemName} x ${item.quantity} - ₹${item.discountedPrice * item.quantity}",
                        modifier = Modifier.padding(8.dp), color = Color.Black
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Confirm order button
        Button(
            onClick = {
                val orderId = System.currentTimeMillis().toString() // Generate unique order ID
                val orderData = hashMapOf(
                    "orderId" to orderId,
                    "userName" to userName,
                    "userEmail" to userEmail,
                    "phoneNumber" to phoneNumber,
                    "shopName" to shopName,
                    "userAddress" to userAddress,
                    "items" to cartItems.map {
                        mapOf(
                            "itemId" to it.itemId,
                            "itemName" to it.itemName,
                            "quantity" to it.quantity,
                            "discountedPrice" to it.discountedPrice,
                            "realPrice" to it.realPrice
                        )
                    },
                    "status" to "Pending" // Initial status of the order
                )

                // Save order to Firestore
                val db = FirebaseFirestore.getInstance()
                db.collection("orders").document(orderId)
                    .set(orderData)
                    .addOnSuccessListener {
                        Toast.makeText(context, "Order placed successfully!", Toast.LENGTH_SHORT)
                            .show()
                        navController.navigate("order_confirmation")
                    }
                    .addOnFailureListener {
                        Toast.makeText(context, "Failed to place order.", Toast.LENGTH_SHORT).show()
                    }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp)
        ) {
            Text(text = "Confirm Order", fontSize = 20.sp)
        }
    }
}
