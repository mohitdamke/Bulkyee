package com.example.bulkyee.screens

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.bulkyee.navigation.Routes
import com.example.bulkyee.ui.theme.White10
import com.example.bulkyee.viewmodel.LoginViewModel
import com.example.bulkyee.viewmodel.ProfileViewModel
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    modifier: Modifier = Modifier,
    navController: NavController
) {
    val loginViewModel: LoginViewModel = viewModel()
    val profileViewModel: ProfileViewModel = viewModel()
    val context = LocalContext.current
    val firebaseAuth = FirebaseAuth.getInstance()
    val currentUser = firebaseAuth.currentUser
    val currentUserId = currentUser?.uid

    var isLoading by remember { mutableStateOf(true) } // Loading state to control navigation
    var hasNavigated by remember { mutableStateOf(false) } // Navigation flag

    // States for fetched user details
    var name by remember { mutableStateOf("") }
    var shopName by remember { mutableStateOf("") }
    var phoneNumber by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }

    // Fetch user details when the splash screen is displayed
    LaunchedEffect(currentUserId) {
        if (!hasNavigated) {
            if (currentUserId != null) {
                // User is logged in, navigate directly to the Home Screen
                delay(1000L)
                Log.d("SplashScreen", "User is logged in, navigating to HomeScreen directly.")
                navController.navigate(Routes.HomeScreen.routes) {
                    popUpTo(0) // Clear backstack
                }
                hasNavigated = true
                isLoading = false
            } else {
                // User is not logged in, navigate to Login Screen
                delay(1000L)

                Log.d("SplashScreen", "User not logged in, navigating to LoginScreen.")
                navController.navigate(Routes.LoginScreen.routes) {
                    popUpTo(0) // Clear backstack
                }
                hasNavigated = true
                isLoading = false
            }
        }
    }

    // Splash Screen UI
    if (isLoading) {
        Column(
            modifier = modifier
                .fillMaxSize()
                .background(White10)
                .padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(text = "Bulkyee")
        }
    }
}

