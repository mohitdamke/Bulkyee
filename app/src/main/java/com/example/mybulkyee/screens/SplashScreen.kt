package com.example.mybulkyee.screens

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.mybulkyee.R
import com.example.mybulkyee.navigation.Routes
import com.example.mybulkyee.ui.theme.White10
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    modifier: Modifier = Modifier,
    navController: NavController
) {
    val currentUser = FirebaseAuth.getInstance().currentUser

    LaunchedEffect(currentUser) {
        delay(1000L) // Delay for the splash screen
        if (currentUser != null) {
            Log.d("SplashScreen", "User is logged in, navigating to HomeScreen.")
            navController.navigate(Routes.HomeScreen.routes) {
                popUpTo(Routes.SplashScreen.routes) { inclusive = true }
            }
        } else {
            Log.d("SplashScreen", "User not logged in, navigating to LoginScreen.")
            navController.navigate(Routes.LoginScreen.routes) {
                popUpTo(Routes.SplashScreen.routes) { inclusive = true }
            }
        }
    }

    // Splash Screen UI
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(White10)
            .padding(10.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.whitelogo),
            contentDescription = "white logo",
            modifier = modifier.size(200.dp)
        )
    }
}
