package com.example.bulkyee.screens

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.rounded.Person2
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.bulkyee.dimensions.FamilyDim
import com.example.bulkyee.dimensions.FontDim
import com.example.bulkyee.navigation.Routes
import com.example.bulkyee.viewmodel.LoginViewModel
import com.google.firebase.auth.FirebaseAuth

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InformationScreen(modifier: Modifier = Modifier, navController: NavController) {

    val context = LocalContext.current
    val loginViewModel: LoginViewModel = viewModel()
    val firebase = FirebaseAuth.getInstance()
    val currentUserId = firebase.currentUser!!.uid
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())
    var name by remember { mutableStateOf("") }
    var shopName by remember { mutableStateOf("") }
    var phoneNumber by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }


    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.Black,
                    titleContentColor = Color.White,
                    actionIconContentColor = Color.White,
                    navigationIconContentColor = Color.White,
                    scrolledContainerColor = Color.Black,
                ),
                title = {
                    Text(
                        text = "Enter your credentials",
                        maxLines = 1,
                        letterSpacing = 1.sp,
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
            FloatingActionButton(
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
                        loginViewModel.updateUserDetails(
                            name = name,
                            shopName = shopName, phone = phoneNumber, address = address, userId = currentUserId
                        )
                        navController.navigate(Routes.HomeScreen.routes)
                        Toast.makeText(context, "Details saved successfully!", Toast.LENGTH_SHORT)
                            .show()
                    }
                },
                //                    navController.navigate(Routes.HomeScreen.routes)
                containerColor = Color.Black,
                contentColor = Color.White,
            ) {
                Icon(Icons.Filled.Add, contentDescription = "Add Item")
            }
        }
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(10.dp), verticalArrangement = Arrangement.Center
        ) {

            TextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Name") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            TextField(
                value = shopName,
                onValueChange = { shopName = it },
                label = { Text("Shop Name") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            TextField(
                value = phoneNumber,
                onValueChange = { phoneNumber = it },
                label = { Text("Phone Number") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone)
            )
            Spacer(modifier = Modifier.height(8.dp))
            TextField(
                value = address,
                onValueChange = { address = it },
                label = { Text("Address") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))

        }

}
}