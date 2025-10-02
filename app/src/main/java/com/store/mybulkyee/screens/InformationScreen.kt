package com.store.mybulkyee.screens

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Numbers
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Shop
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.store.mybulkyee.data.PreferencesHelper
import com.store.mybulkyee.dimensions.FamilyDim
import com.store.mybulkyee.dimensions.FontDim
import com.store.mybulkyee.navigation.Routes
import com.store.mybulkyee.ui.theme.Brown40
import com.store.mybulkyee.ui.theme.White10
import com.store.mybulkyee.viewmodel.LoginViewModel
import com.store.mybulkyee.viewmodel.ProfileViewModel
import com.google.firebase.auth.FirebaseAuth

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InformationScreen(modifier: Modifier = Modifier, navController: NavController) {

    val context = LocalContext.current
    val loginViewModel: LoginViewModel = hiltViewModel()
    val profileViewModel: ProfileViewModel = hiltViewModel()
    val firebase = FirebaseAuth.getInstance()
    val currentUserId = firebase.currentUser?.uid
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())

    var name by remember { mutableStateOf("") }
    var shopName by remember { mutableStateOf("") }
    var phoneNumber by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    val currentEmail = firebase.currentUser?.email

    if (currentUserId == null || currentEmail == null) {

        Toast.makeText(context, "User not logged in!", Toast.LENGTH_SHORT).show()
        Log.e("InformationScreen", "User not logged in. UID: $currentUserId, Email: $currentEmail")
        return
    }

    // Fetch user details from Firestore if available
    LaunchedEffect(currentUserId) {
        profileViewModel.fetchUserDetails(
            context = context
        ) { fetchedName, fetchedShopName, fetchedPhoneNumber, fetchedAddress, fetchedEmail ->
            // Only update the fields if they are empty
            if (name.isBlank()) name = fetchedName
            if (shopName.isBlank()) shopName = fetchedShopName
            if (phoneNumber.isBlank()) phoneNumber = fetchedPhoneNumber
            if (address.isBlank()) address = fetchedAddress
        }
    }

    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            CenterAlignedTopAppBar(
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = White10,
                    actionIconContentColor = Color.White,
                    navigationIconContentColor = Color.White,
                    scrolledContainerColor = Color.Transparent,
                ),
                title = {
                    Text(
                        text = "Enter your credentials",
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

                )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = {
                    if (name.isBlank() || shopName.isBlank() || phoneNumber.isBlank() || address.isBlank()) {
                        Toast.makeText(context, "Please fill all fields!", Toast.LENGTH_SHORT)
                            .show()
                    } else if (!phoneNumber.matches("\\d{10}".toRegex())) {
                        Toast.makeText(
                            context,
                            "Enter a valid 10-digit phone number!",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        profileViewModel.saveUserDetails(
                            name = name,
                            shopName = shopName,
                            phoneNumber = phoneNumber,
                            address = address,
                            email = currentEmail
                        )
                        // Save to SharedPreferences
                        PreferencesHelper.saveUserInfo(
                            context = context,
                            name = name,
                            shopName = shopName,
                            phoneNumber = phoneNumber,
                            address = address,
                            email = firebase.currentUser?.email ?: "No Email",
                            keyUser = true
                        )
                        // Navigate to the HomeScreen
                        navController.navigate(Routes.HomeScreen.routes) {
                            popUpTo(Routes.InformationScreen.routes) { inclusive = true }
                            launchSingleTop = true
                        }
                        Toast.makeText(context, "Details saved successfully!", Toast.LENGTH_SHORT)
                            .show()
                    }
                },
                containerColor = Brown40,
                contentColor = Color.White,
                icon = { Icon(Icons.Filled.DoneAll, "Done.") },
                text = {
                    Text(
                        text = "Information added", fontSize = FontDim.mediumTextSize,
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
                .padding(10.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            InformationOutlineText(
                value = name,
                onValueChange = { name = it },
                label = "Name",
                icons = Icons.Default.Person,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text, imeAction = ImeAction.Next
                )
            )

            Spacer(modifier = Modifier.height(8.dp))
            InformationOutlineText(
                value = phoneNumber,
                onValueChange = { phoneNumber = it },
                label = "Phone Number",
                icons = Icons.Default.Numbers,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number, imeAction = ImeAction.Next
                )
            )

            Spacer(modifier = Modifier.height(8.dp))
            InformationOutlineText(
                value = shopName,
                onValueChange = { shopName = it },
                label = "Shop Name",
                icons = Icons.Default.Shop,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text, imeAction = ImeAction.Next
                )
            )

            Spacer(modifier = Modifier.height(8.dp))
            InformationOutlineText(
                value = address,
                onValueChange = { address = it },
                label = "Address",
                icons = Icons.Default.LocationOn,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text, imeAction = ImeAction.Done
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

        }

    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun InformationOutlineText(
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
                tint = Color.Black
            )
        },
        placeholder = {
            Text(
                text = "Enter your $label",
                fontSize = FontDim.mediumTextSize,
                fontFamily = FamilyDim.Bold,
                color = Color.Gray,
                maxLines = 1,
                overflow = TextOverflow.Visible
            )
        },
        colors = OutlinedTextFieldDefaults.colors(
            unfocusedPlaceholderColor = Color.Gray,
            focusedPlaceholderColor = Color.Gray,
            focusedBorderColor = Color.Gray,
            unfocusedBorderColor = Color.Gray,
            focusedTextColor = Color.Black,
            unfocusedTextColor = Color.Black
        ),
        keyboardOptions = keyboardOptions,
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(100.dp),
        minLines = 1
    )
}