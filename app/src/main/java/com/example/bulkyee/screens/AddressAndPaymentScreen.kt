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
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.bulkyee.data.Item
import com.example.bulkyee.data.PreferencesHelper
import com.example.bulkyee.viewmodel.OrderViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun AddressAndPaymentScreen(
    navController: NavController,
    cartQueryParam: String? = null,
) {

    val context = LocalContext.current
    val orderViewModel: OrderViewModel = viewModel()


    // Logging cart items for debugging
    Log.d("AddressAndPaymentScreen", "Parsed cartItems: $cartQueryParam")
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text("Delivery Address:", fontWeight = FontWeight.Bold, fontSize = 18.sp)
        // Replace with actual user address data
        Text("User Address", modifier = Modifier.padding(8.dp))

        Text("Items:", fontWeight = FontWeight.Bold, fontSize = 18.sp)

        if (cartQueryParam.isNullOrEmpty()) {
            Text("No items in the cart", modifier = Modifier.padding(8.dp))
        } else {
            LazyColumn(modifier = Modifier.weight(1f)) {
                items(cartQueryParam.split(",")) { item ->
                    Text(
                        text = item,
                        modifier = Modifier.padding(8.dp), color = Color.Black
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Confirm order button
        Button(
            onClick = {
                orderViewModel.placeOrder(
                    context,
                    cartQueryParam,
                    navController
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp)
        ) {
            Text(text = "Confirm Order", fontSize = 20.sp)
        }
    }
}
