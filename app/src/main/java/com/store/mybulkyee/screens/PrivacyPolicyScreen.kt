package com.store.mybulkyee.screens

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController

@Composable
fun PrivacyPolicyScreen(modifier: Modifier = Modifier, navController: NavController) {
    val context = LocalContext.current
    val privacyPolicyUrl = "https://sites.google.com/view/bulkyeeapp/home"

    // Automatically open the URL when the screen is opened
    LaunchedEffect(Unit) {
        openPrivacyPolicy(context, privacyPolicyUrl)
        navController.popBackStack() // Navigate back after opening the link
    }
}

fun openPrivacyPolicy(context: Context, url: String) {
    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK) // Required to open in a new activity
    try {
        context.startActivity(intent)
    } catch (e: Exception) {
        e.printStackTrace()
    }
}
