package com.example.bulkyee.screens

import android.app.Activity
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
import com.example.bulkyee.viewmodel.LoginViewModel
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    modifier: Modifier = Modifier,
    navController: NavController
) {

    val loginViewModel: LoginViewModel = viewModel()
    val context = LocalContext.current
    val isLoading by loginViewModel.isLoading.observeAsState(false)
    val isLoggedIn by loginViewModel.isUserLoggedIn.observeAsState(initial = false)
    LaunchedEffect(Unit) {
        if (isLoggedIn) {
            navController.navigate(Routes.InformationScreen.routes) {
                popUpTo(0) // Clear backstack
            }
        }
    }

    // Check if user setup is already completed
    LaunchedEffect(Unit) {
        if (PreferencesHelper.isUserSetupCompleted(context)) {
            navController.navigate(Routes.HomeScreen.routes) {
                popUpTo(0) // Clear backstack
            }
        }
    }
    LaunchedEffect(key1 = Unit) {
        delay(1000L)
        navController.navigate(Routes.LoginScreen.routes) {
            popUpToId
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(10.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "Bulkyee")
    }
}