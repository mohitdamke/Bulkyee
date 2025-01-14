package com.example.bulkyee.screens

import android.app.Activity
import android.widget.Toast
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.bulkyee.data.PreferencesHelper
import com.example.bulkyee.navigation.Routes
import com.example.bulkyee.ui.theme.White10
import com.example.bulkyee.viewmodel.LoginViewModel
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    modifier: Modifier = Modifier,
    navController: NavController
) {

    val loginViewModel: LoginViewModel = viewModel()
    val context = LocalContext.current
    val isLoggedIn by loginViewModel.isUserLoggedIn.observeAsState(initial = false)
    val firebaseUser = Firebase.auth.currentUser?.uid


    // Check if user setup is already completed
    LaunchedEffect(isLoggedIn) {
        // Delay for splash screen effect

        if (firebaseUser != null) {
            // User is logged in, navigate to Information Screen
            navController.navigate(Routes.InformationScreen.routes) {
                popUpTo(0) // Clear backstack
            }
        } else if (PreferencesHelper.isUserSetupCompleted(context)) {
            navController.navigate(Routes.HomeScreen.routes) {
                popUpTo(0) // Clear backstack
            }
        } else {
            navController.navigate(Routes.LoginScreen.routes) {
                popUpTo(0) // Clear backstack
            }
        }
    }

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