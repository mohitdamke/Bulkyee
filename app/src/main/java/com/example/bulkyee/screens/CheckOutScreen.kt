package com.example.bulkyee.screens

import android.Manifest
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.bulkyee.BuildConfig
import com.example.bulkyee.R
import com.example.bulkyee.createNotificationChannel
import com.example.bulkyee.data.Item
import com.example.bulkyee.data.PreferencesHelper
import com.example.bulkyee.dimensions.FamilyDim
import com.example.bulkyee.dimensions.FontDim
import com.example.bulkyee.navigation.Routes
import com.example.bulkyee.showOrderNotification
import com.example.bulkyee.ui.theme.Brown40
import com.example.bulkyee.ui.theme.White10
import com.example.bulkyee.viewmodel.OrderViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
fun CheckOutScreen(
    modifier: Modifier = Modifier,
    cartQueryParam: String?,
    navController: NavController,
) {

    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())
    val context = LocalContext.current
    val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
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

    // Location Check

    val targetLat = BuildConfig.TARGET_LAT
    val targetLng = BuildConfig.TARGET_LNG


    var isWithinRadius by remember { mutableStateOf(false) }

    val locationPermissionState = rememberPermissionState(Manifest.permission.ACCESS_FINE_LOCATION)
    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                location?.let {
                    isWithinRadius = isUserWithinDeliveryRadius(
                        userLat = location.latitude,
                        userLng = location.longitude,
                        targetLat = targetLat,
                        targetLng = targetLng,
                        radius = 1000.0 // 1 km radius
                    )

                    // Trigger a notification based on delivery eligibility
                    sendDeliveryEligibilityNotification(context, isWithinRadius)
                }
            }
        }
    }

    LaunchedEffect(Unit) {
        createNotificationChannel(context)
        if (!locationPermissionState.status.isGranted) {
            locationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        } else {
            if (ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return@LaunchedEffect
            }
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                location?.let {
                    isWithinRadius = isUserWithinDeliveryRadius(
                        userLat = location.latitude,
                        userLng = location.longitude,
                        targetLat = targetLat,
                        targetLng = targetLng,
                        radius = 1000.0 // 1 km radius
                    )
                    sendDeliveryEligibilityNotification(context, isWithinRadius)
                }
            }
        }
    }
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
                    text = "Items:", fontFamily = FamilyDim.Bold,
                    color = Color.Black,
                    fontSize = FontDim.largeTextSize,
                )

                if (cartQueryParam.isNullOrEmpty()) {
                    Text(
                        text = "No items in the cart",
                        color = Color.Black,
                        modifier = Modifier.padding(8.dp)
                    )
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
                    color = Color.Black,
                    fontSize = FontDim.largeTextSize,
                    fontFamily = FamilyDim.SemiBold,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                // Check eligibility to place an order
                if (!isWithinRadius) {
                    Text(
                        text = "You are outside the delivery area. Delivery is available only within a 1 km radius.",
                        color = Color.Red,
                        fontSize = FontDim.mediumTextSize,
                        fontFamily = FamilyDim.SemiBold,
                        modifier = Modifier.padding(10.dp)
                    )
                } else {
                    Button(
                        onClick = {
                            scope.launch {
                                Log.d("CheckOutScreen", "Place Order button clicked")
                                if (cartItems.isNotEmpty()) {
                                    orderViewModel.placeOrder(
                                        context = context,
                                        cartQueryParam = cartQueryParam,
                                        navController = navController,
                                        totalPrice = totalPrice
                                    )

                                    Toast.makeText(
                                        context,
                                        "Order is been placed successfully",
                                        Toast.LENGTH_LONG
                                    ).show()

                                    showOrderNotification(context)

                                    navController.navigate(Routes.HomeScreen.routes) {
                                        popUpTo(navController.graph.startDestinationId) {
                                            inclusive = true
                                        } // This will clear the back stack and ensure no back navigation
                                    }
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
                            color = Color.Black,
                            fontFamily = FamilyDim.SemiBold,
                        )
                    }
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
                color = Color.Black,
                fontFamily = FamilyDim.Normal,
            )
            Text(
                text = "Real Price: ₹${item.realPrice}", fontSize = FontDim.mediumTextSize,
                color = Color.Black,
                fontFamily = FamilyDim.Normal, textDecoration = TextDecoration.LineThrough
            )
            Text(
                text = "Discounted Price: ₹${item.discountedPrice}",
                color = Color.Black,
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
            color = Color.Black,
            fontSize = FontDim.largeTextSize
        )
        Text(
            text = address,
            color = Color.Black,
            fontFamily = FamilyDim.Medium,
            fontSize = FontDim.mediumTextSize
        )
        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = "Name:",
            fontFamily = FamilyDim.SemiBold,
            color = Color.Black,
            fontSize = FontDim.largeTextSize
        )
        Text(
            text = name,
            fontFamily = FamilyDim.Medium,
            color = Color.Black,
            fontSize = FontDim.mediumTextSize,
            modifier = Modifier.padding(vertical = 4.dp)
        )
        Spacer(modifier = Modifier.height(10.dp))


        Text(
            text = "Shop Name:",
            fontFamily = FamilyDim.SemiBold,
            color = Color.Black,
            fontSize = FontDim.largeTextSize
        )
        Text(
            text = shopName,
            fontFamily = FamilyDim.Medium,
            color = Color.Black,
            fontSize = FontDim.mediumTextSize,
            modifier = Modifier.padding(vertical = 4.dp)
        )
        Spacer(modifier = Modifier.height(10.dp))


        Text(
            text = "Email:",
            fontFamily = FamilyDim.SemiBold,
            color = Color.Black,
            fontSize = FontDim.largeTextSize
        )
        Text(
            text = email,
            fontFamily = FamilyDim.Medium,
            color = Color.Black,
            fontSize = FontDim.mediumTextSize,
            modifier = Modifier.padding(vertical = 4.dp)
        )

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = "Phone No:",
            color = Color.Black,
            fontFamily = FamilyDim.SemiBold,
            fontSize = FontDim.largeTextSize
        )
        Text(
            text = phoneNumber,
            color = Color.Black,
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

// In CheckOutScreen.kt
fun isUserWithinDeliveryRadius(
    userLat: Double,
    userLng: Double,
    targetLat: Double,
    targetLng: Double,
    radius: Double
): Boolean {
    val results = FloatArray(1)
    Location.distanceBetween(userLat, userLng, targetLat, targetLng, results)
    return results[0] <= radius
}


fun sendDeliveryEligibilityNotification(context: Context, isEligible: Boolean) {
    val notificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    val notificationId = 1
    val notificationTitle = if (isEligible) "Delivery Available" else "Delivery Not Available"
    val notificationMessage = if (isEligible) {
        "You are within the delivery area. Proceed with your order."
    } else {
        "Sorry, you are outside the delivery area."
    }

    val notification = NotificationCompat.Builder(context, "delivery_channel")
        .setContentTitle(notificationTitle)
        .setContentText(notificationMessage)
        .setSmallIcon(R.drawable.google) // Replace with your icon
        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
        .build()

    notificationManager.notify(notificationId, notification)
}