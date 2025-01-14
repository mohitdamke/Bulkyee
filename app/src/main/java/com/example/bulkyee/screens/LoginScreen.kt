package com.example.bulkyee.screens

import android.app.Activity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.bulkyee.R
import com.example.bulkyee.data.PreferencesHelper
import com.example.bulkyee.dimensions.FamilyDim
import com.example.bulkyee.dimensions.FontDim
import com.example.bulkyee.navigation.Routes
import com.example.bulkyee.ui.theme.Brown40
import com.example.bulkyee.ui.theme.White10
import com.example.bulkyee.viewmodel.LoginViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.common.api.ApiException
import com.google.firebase.Firebase
import com.google.firebase.auth.auth

@Composable
fun LoginScreen(modifier: Modifier = Modifier, navController: NavController) {

    val loginViewModel: LoginViewModel = viewModel()
    val currentUserId = Firebase.auth.currentUser?.uid

    val context = LocalContext.current
    val activity = LocalContext.current as Activity
    val isLoading by loginViewModel.isLoading.observeAsState(false)
    val isLoggedIn by loginViewModel.isUserLoggedIn.observeAsState(initial = false)


    LaunchedEffect(isLoggedIn) {
        // Check if the user is logged in via Firebase
        val firebaseUser = Firebase.auth.currentUser

        if (firebaseUser != null) {
            // User is logged in, navigate to Information Screen
            navController.navigate(Routes.InformationScreen.routes) {
                popUpTo(0) // Clear backstack
            }
        } else {
            // If user is not logged in, check setup completion and navigate accordingly
            if (PreferencesHelper.isUserSetupCompleted(context)) {
                navController.navigate(Routes.HomeScreen.routes) {
                    popUpTo(0) // Clear backstack
                }
            }
        }
    }


    // Result launcher to handle Google Sign-In
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
        try {
            val account = task.getResult(ApiException::class.java)
            loginViewModel.firebaseAuthWithGoogle(account) { success ->
                if (success) {
                    navController.navigate(Routes.InformationScreen.routes) {
                        popUpTo(0) // Clear backstack
                    }
                } else {
                    loginViewModel.showErrorMessage(
                        activity,
                        "Authentication failed. Please try again."
                    )
                }
            }
        } catch (e: ApiException) {
            loginViewModel.showErrorMessage(
                activity,
                "Google Sign-In failed: ${e.localizedMessage}"
            )
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(White10)
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.weight(1f))

        Image(
            painter = painterResource(id = R.drawable.ic_launcher_foreground),
            contentDescription = "App Logo",
            modifier = Modifier.size(100.dp)
        )
        Spacer(modifier = Modifier.height(20.dp))
        Text(
            text = "Bulkyee",
            fontSize = FontDim.extraLargeTextSize,
            fontFamily = FamilyDim.Bold,
            color = Brown40
        )

        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Order product easily with one click",
            fontSize = FontDim.smallTextSize,
            fontFamily = FamilyDim.Normal,
            color = Color.Gray
        )

        Spacer(modifier = Modifier.weight(1f))
        Button(
            onClick = {
                val signInIntent = loginViewModel.getGoogleSignInClient(activity).signInIntent
                launcher.launch(signInIntent)
            },
            colors = ButtonDefaults.buttonColors(Brown40),
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.google),
                    contentDescription = "Google Icon",
                    modifier = Modifier.size(30.dp)
                )
                Spacer(modifier = Modifier.padding(8.dp))
                Text(
                    text = "Sign in with Google",
                        fontSize = FontDim.smallTextSize,
                        fontFamily = FamilyDim.Normal,
                        color = Color.White

                    )
            }
        }
        Spacer(modifier = Modifier.weight(1f))
    }
    if (isLoading) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.5f)),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
        }
    }
}
