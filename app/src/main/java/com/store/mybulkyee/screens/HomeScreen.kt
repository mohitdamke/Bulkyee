package com.store.mybulkyee.screens

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ContactPage
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PrivacyTip
import androidx.compose.material.icons.filled.ProductionQuantityLimits
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.outlined.ContactPage
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.PrivacyTip
import androidx.compose.material.icons.outlined.ProductionQuantityLimits
import androidx.compose.material.icons.rounded.Menu
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DrawerDefaults
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Black
import androidx.compose.ui.graphics.Color.Companion.Gray
import androidx.compose.ui.graphics.Color.Companion.Transparent
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.app.ActivityCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.store.mybulkyee.BuildConfig
import com.store.mybulkyee.R
import com.store.mybulkyee.data.Item
import com.store.mybulkyee.data.NavigationItems
import com.store.mybulkyee.dimensions.FamilyDim
import com.store.mybulkyee.dimensions.FontDim
import com.store.mybulkyee.navigation.Routes
import com.store.mybulkyee.ui.theme.Brown40
import com.store.mybulkyee.ui.theme.White10
import com.store.mybulkyee.viewmodel.CartViewModel
import com.store.mybulkyee.viewmodel.HomeViewModel
import com.store.mybulkyee.viewmodel.LoginViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import kotlinx.coroutines.launch
import java.net.URLEncoder

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
fun HomeScreen(modifier: Modifier = Modifier, navController: NavController) {
    val targetLat = BuildConfig.TARGET_LAT
    val targetLng = BuildConfig.TARGET_LNG

    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())
    val context = LocalContext.current
    val homeViewModel: HomeViewModel = hiltViewModel()
    val loginViewModel: LoginViewModel = hiltViewModel()
    val cartViewModel: CartViewModel = hiltViewModel()
    val isLoggedIn by loginViewModel.isUserLoggedIn.observeAsState(initial = false)
    val selectedItems by cartViewModel.selectedItems.collectAsState()
    var showEligibilityDialog by remember { mutableStateOf(false) }
    val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager

    // Search query state
    var searchQuery by remember { mutableStateOf("") }

    val isSuccess by homeViewModel.isSuccess.collectAsState(false)
    val isError by homeViewModel.isError.collectAsState(false)
    val items = homeViewModel.items.collectAsState().value

    // Filtered items based on search query
    val filteredItems = items.filter { it.itemName.contains(searchQuery, ignoreCase = true) }

    // Pull-to-refresh state
    val isLoading by homeViewModel.isLoading.collectAsState(false)
    val scope = rememberCoroutineScope()

    val adId = "ca-app-pub-3940256099942544/9214589741"


    val locationPermissionState = rememberPermissionState(Manifest.permission.ACCESS_FINE_LOCATION)

    val fusedLocationClient: FusedLocationProviderClient = remember {
        LocationServices.getFusedLocationProviderClient(context)
    }

    // Distance check state
    var isWithinRadius by remember { mutableStateOf(false) }

    // Location services and permission check
    LaunchedEffect(Unit) {
        // Check if location services are enabled
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            // Check if permission is granted
            if (locationPermissionState.status.isGranted) {
                // Permission granted, proceed with location fetch
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
                        // Check if the user is within the delivery radius
                        isWithinRadius = isUserWithinRadius(
                            userLat = it.latitude,
                            userLng = it.longitude,
                            targetLat = targetLat,
                            targetLng = targetLng,
                            radius = 1000.0 // 1 km radius
                        )
                        showEligibilityDialog = !isWithinRadius
                    }
                }
            } else {
                // Request location permission if not granted
                locationPermissionState.launchPermissionRequest()
            }
        }
    }

    // Handle login state changes and navigation
    LaunchedEffect(isLoggedIn) {
        val firebaseUser = Firebase.auth.currentUser
        if (firebaseUser == null) {
            // Navigate to the SplashScreen if the user is not logged in
            navController.navigate(Routes.SplashScreen.routes) {
                popUpTo(0) // Clear backstack
            }
        }
    }

    // Fetch items when the home screen is loaded
    LaunchedEffect(key1 = homeViewModel) {
        scope.launch {
            homeViewModel.fetchItems()
        }
    }


    ///List of Navigation Items that will be clicked
    val navigationItems = listOf(
        NavigationItems(
            title = "Home",
            selectedIcon = Icons.Filled.Home,
            unselectedIcon = Icons.Outlined.Home,
            route = Routes.HomeScreen.routes
        ), NavigationItems(
            title = "Profile",
            selectedIcon = Icons.Filled.Person,
            unselectedIcon = Icons.Outlined.Person,
            route = Routes.ProfileScreen.routes
        ), NavigationItems(
            title = "My Orders",
            selectedIcon = Icons.Filled.ProductionQuantityLimits,
            unselectedIcon = Icons.Outlined.ProductionQuantityLimits,
            route = Routes.MyOrderScreen.routes,
        ), NavigationItems(
            title = "Privacy Policy",
            selectedIcon = Icons.Filled.PrivacyTip,
            unselectedIcon = Icons.Outlined.PrivacyTip,
            route = Routes.PrivacyPolicy.routes
        ), NavigationItems(
            title = "Contact Us",
            selectedIcon = Icons.Filled.ContactPage,
            unselectedIcon = Icons.Outlined.ContactPage,
            route = Routes.ContactUsScreen.routes
        )
    )

    //Remember Clicked index state
    var selectedItemIndex by rememberSaveable {
        mutableIntStateOf(0)
    }


    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    // Check if location services are enabled
    // Show eligibility dialog based on location and radius check
    if (showEligibilityDialog) {
        AlertDialog(
            onDismissRequest = { showEligibilityDialog = false },
            title = {
                Text(
                    text = "Order Eligibility Status",
                    fontSize = FontDim.largeTextSize,
                    fontFamily = FamilyDim.Bold,
                    color = Black
                )
            },
            text = {
                Text(
                    text = if (isWithinRadius) {
                        "You are eligible to place an order as you are within the delivery area."
                    } else {
                        "You are not eligible to place an order as you are outside the delivery area."
                    },
                    fontSize = FontDim.mediumTextSize,
                    fontFamily = FamilyDim.Normal
                )
            },
            confirmButton = {
                Button(
                    onClick = { showEligibilityDialog = false },
                    colors = ButtonDefaults.buttonColors(Brown40)
                ) {
                    Text(
                        text = "OK",
                        fontSize = FontDim.mediumTextSize,
                        fontFamily = FamilyDim.Normal,
                        color = White
                    )
                }
            },
            containerColor = White10,
            titleContentColor = Black,
            textContentColor = Black
        )
    }

    ModalNavigationDrawer(
        scrimColor = DrawerDefaults.scrimColor, drawerState = drawerState, drawerContent = {
            ModalDrawerSheet(
                modifier = Modifier.background(White10) // Set the background color
            ) {
                Spacer(modifier = Modifier.height(16.dp)) // Space (margin) from top

                navigationItems.forEachIndexed { index, item ->
                    NavigationDrawerItem(label = {
                        Text(
                            text = item.title,
                            fontFamily = FamilyDim.Normal,
                            fontSize = FontDim.mediumTextSize
                        )
                    }, selected = index == selectedItemIndex, onClick = {
                        selectedItemIndex = index
                        navController.navigate(item.route) {
                            // Optional: Pop up the back stack to avoid navigating back to this screen
                            launchSingleTop = true
                            restoreState = true
                        }
                        scope.launch {
                            drawerState.close()
                        }
                    }, icon = {
                        Icon(
                            imageVector = if (index == selectedItemIndex) {
                                item.selectedIcon
                            } else item.unselectedIcon,
                            contentDescription = item.title,
                            tint = Brown40 // Set the icon color
                        )
                    }, badge = { // Show Badge
                        item.badgeCount?.let {
                            Text(text = item.badgeCount.toString())
                        }
                    }, modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                    )
                }
            }
        }, gesturesEnabled = true
    ) {
        Scaffold(modifier = modifier
            .fillMaxSize()
            .background(White10)
            .nestedScroll(scrollBehavior.nestedScrollConnection), topBar = {
            CenterAlignedTopAppBar(
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = White10,
                    actionIconContentColor = White,
                    navigationIconContentColor = White,
                    scrolledContainerColor = Transparent,
                ),
                title = {
                    Text(
                        text = "Bulkyee",
                        maxLines = 1,
                        letterSpacing = 1.sp,
                        color = Brown40,
                        textAlign = TextAlign.Center,
                        fontSize = FontDim.extraLargeTextSize,
                        overflow = TextOverflow.Visible,
                        fontFamily = FamilyDim.Bold,
                    )
                },
                scrollBehavior = scrollBehavior,
                actions = {
                    // Search Bar UI

                    Spacer(modifier = Modifier.padding(10.dp))
                    Icon(
                        Icons.Rounded.Menu,
                        contentDescription = "",
                        modifier = Modifier
                            .size(30.dp)
                            .clickable {
                                scope.launch {
                                    drawerState.apply {
                                        if (isClosed) open() else close()
                                    }
                                }
                            },
                        tint = Brown40
                    )
                },
            )
        }, bottomBar = {
            AndroidView(
                modifier = modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .padding(2.dp),
                factory = { context ->
                    AdView(context).apply {
                        setAdSize(AdSize.BANNER)
                        adUnitId = adId

                        loadAd(AdRequest.Builder().build())
                    }
                }
            )
        }, floatingActionButton = {
            ExtendedFloatingActionButton(

                onClick = {
                    val cartItems = cartViewModel.getCartItems(items)
//                    val cartItems = items.filter { (selectedItems[it.itemId] ?: 0) > 0 }
                    val cartData = cartItems.joinToString(",") {
                        "${it.itemId}:${selectedItems[it.itemId] ?: 0}:${it.itemName}:${it.discountedPrice}:${it.realPrice}"
                    }
                    val encodedCartData = URLEncoder.encode(cartData, "UTF-8")
                    navController.navigate(
                        Routes.CheckOutScreen.routes.replace(
                            oldValue = "{cartQueryParam}", newValue = encodedCartData
                        )
                    )
                },
                containerColor = Brown40,
                contentColor = White,
                icon = { Icon(Icons.Filled.ShoppingCart, "Proceed Buying.") },
                text = {
                    Text(
                        text = "Proceed Buying",
                        fontSize = FontDim.mediumTextSize,
                        fontFamily = FamilyDim.Normal
                    )
                },
            )
        }

        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(White10)
                    .padding(paddingValues)
                    .padding(10.dp)
            ) {
                SearchOutlineText(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    label = "Search items",
                    icons = Icons.Default.Search,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Text, imeAction = ImeAction.Search
                    )
                )
                Spacer(modifier = modifier.padding(10.dp))

                if (isLoading) {
                    // Show loading indicator
                    Box(modifier = modifier.fillMaxSize()) {
                        CircularProgressIndicator(
                            modifier = Modifier
                                .align(Alignment.Center)
                                .size(50.dp)
                        ) // Adjust the size to prevent it from taking full size)
                    }
                }

                if (isError && items.isEmpty()) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(White10)
                            .padding(10.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        // Show error message
                        Text(
                            text = "An error occurred while adding the item. Please try again.",
                            color = Color.Red,
                            fontSize = FontDim.mediumTextSize,
                            fontFamily = FamilyDim.Medium,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }


                // Show loading or no items message based on the state of the items list
                if (isSuccess && items.isEmpty()) {

                    // Show "No items available" message
                    // Show a message indicating no items are available
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(White10),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {

                        Text(
                            text = "No items available.",
                            fontSize = FontDim.mediumTextSize,
                            fontFamily = FamilyDim.Bold,
                            color = Black
                        )
                    }
                } else if (isSuccess) {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(White10)
                    ) {
                        items(filteredItems) { item ->
                            ItemCard(item = item,
                                quantity = selectedItems[item.itemId] ?: 0,
                                onQuantityChange = { newQuantity ->
                                    cartViewModel.updateItemQuantity(item.itemId, newQuantity)
//                                onQuantityChange = { newQuantity ->
//                                    selectedItems[item.itemId] = newQuantity
                                }
                            )
                        }

                    }
                }
            }
        }
    }
}


@Composable
fun ItemCard(
    modifier: Modifier = Modifier, item: Item, quantity: Int, onQuantityChange: (Int) -> Unit
) {
    val cartViewModel: CartViewModel = viewModel()
    val scope = rememberCoroutineScope()

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp),
        colors = CardDefaults.cardColors(White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier.padding(10.dp), verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = rememberAsyncImagePainter(model = item.imageUrl
                    , error = painterResource(id = R.drawable.ic_launcher_foreground), // your error image
                    placeholder = painterResource(id = R.drawable.ic_launcher_foreground)),
                contentDescription = "Item Image",
                modifier = Modifier
                    .clip(CircleShape)
                    .size(100.dp),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.width(10.dp))
            Column(
                modifier = Modifier
                    .padding(10.dp)
                    .weight(1f)
            ) {
                Text(
                    text = item.itemName,
                    fontSize = FontDim.largeTextSize,
                    fontFamily = FamilyDim.SemiBold,
                    color = Black

                )
                Spacer(modifier = Modifier.height(6.dp))

                if (item.itemDesc != "") {
                    Text(
                        text = item.itemDesc,
                        fontSize = FontDim.mediumTextSize,
                        fontFamily = FamilyDim.Normal,
                        color = Black,
                        maxLines = 2

                    )
                    Spacer(modifier = Modifier.height(6.dp))
                }
                Row(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "₹${item.discountedPrice}",
                        fontSize = FontDim.mediumTextSize,
                        fontFamily = FamilyDim.SemiBold,
                        color = Black,
                        modifier = Modifier.padding(end = 10.dp)
                    )
                    Text(
                        text = "₹${item.realPrice}",
                        fontSize = FontDim.smallTextSize,
                        fontFamily = FamilyDim.Normal,
                        textDecoration = TextDecoration.LineThrough,
                        color = Gray
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier
                        .padding(4.dp)
                        .padding(horizontal = 4.dp, vertical = 2.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    // Minus Button
                    IconButton(
                        onClick = {
                            scope.launch {

                                cartViewModel.updateItemQuantity(item.itemId, quantity - 1)
//                            if (quantity > 0) onQuantityChange(quantity - 1)
                            }
                        },
                        modifier = Modifier
                            .size(20.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(Brown40)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Remove,
                            contentDescription = "Decrease Quantity",
                            tint = Color.White
                        )
                    }
                    Spacer(modifier = modifier.padding(10.dp))
                    // Quantity Text
                    Text(
                        text = "$quantity",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Black,
                        modifier = Modifier.padding(horizontal = 8.dp),
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = modifier.padding(10.dp))

                    // Add Button
                    IconButton(
                        onClick = {
                            scope.launch {
                                cartViewModel.updateItemQuantity(item.itemId, quantity + 1)
                            }
//                            onQuantityChange(quantity + 1)
                        },
                        modifier = Modifier
                            .size(20.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(Brown40)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Increase Quantity",
                            tint = Color.White
                        )
                    }
                }
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SearchOutlineText(
    modifier: Modifier = Modifier,
    value: String,
    icons: ImageVector,
    onValueChange: (String) -> Unit,
    label: String,
    keyboardOptions: KeyboardOptions
) {
    OutlinedTextField(
        value = value,
        onValueChange = { onValueChange(it) },
        singleLine = true,
        leadingIcon = {
            Icon(
                imageVector = icons,
                contentDescription = "",
                modifier = Modifier.padding(10.dp),
                tint = Brown40
            )
        },
        placeholder = {
            Text(
                text = "Enter your $label",
                fontSize = FontDim.mediumTextSize,
                fontFamily = FamilyDim.SemiBold,
                color = Color.Gray,
                maxLines = 1,
                overflow = TextOverflow.Visible
            )
        },
        colors = OutlinedTextFieldDefaults.colors(
            unfocusedPlaceholderColor = Color.Gray,
            focusedPlaceholderColor = Color.Gray,
            focusedBorderColor = Brown40,
            unfocusedBorderColor = Brown40,
            focusedTextColor = Black,
            unfocusedTextColor = Black
        ),
        keyboardOptions = keyboardOptions,
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(100.dp),
        minLines = 1
    )
}


fun isUserWithinRadius(
    userLat: Double,
    userLng: Double,
    targetLat: Double,
    targetLng: Double,
    radius: Double
): Boolean {
    val userLocation = Location("user").apply {
        latitude = userLat
        longitude = userLng
    }
    val targetLocation = Location("target").apply {
        latitude = targetLat
        longitude = targetLng
    }

    return userLocation.distanceTo(targetLocation) <= radius
}

fun checkLocation(
    fusedLocationClient: FusedLocationProviderClient,
    context: Context,
    targetLat: Double,
    targetLng: Double,
    onLocationChecked: (Boolean) -> Unit
) {
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
        return
    }
    fusedLocationClient.lastLocation.addOnSuccessListener { location ->
        if (location != null) {
            val isWithinRadius = isUserWithinRadius(
                userLat = location.latitude,
                userLng = location.longitude,
                targetLat = targetLat,
                targetLng = targetLng,
                radius = 1000.0 // 1 km radius
            )
            onLocationChecked(isWithinRadius)
        }
    }
}
