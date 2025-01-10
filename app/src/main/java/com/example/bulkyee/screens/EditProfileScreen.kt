package com.example.bulkyee.screens

import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.rounded.Menu
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.bulkyee.data.PreferencesHelper
import com.example.bulkyee.viewmodel.ProfileViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileScreen(modifier: Modifier = Modifier, navController: NavController) {

    val profileViewModel: ProfileViewModel = viewModel()
    var name by remember { mutableStateOf("") }
    var shopName by remember { mutableStateOf("") }
    var phoneNumber by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }

    val context = LocalContext.current
    val firebaseUser = FirebaseAuth.getInstance().currentUser
    val userId = firebaseUser!!.uid
    val db = FirebaseFirestore.getInstance()

    // Fetch existing profile data
    LaunchedEffect(true) {
        val userProfile = profileViewModel.fetchUserProfile()
        name = userProfile["name"] ?: ""
        shopName = userProfile["shopName"] ?: ""
        phoneNumber = userProfile["phoneNumber"] ?: ""
        address = userProfile["address"] ?: ""
    }

    // Handle update button click
    fun updateProfile() {
        profileViewModel.updateProfile(
            name = name,
            shopName = shopName,
            phoneNumber = phoneNumber,
            address = address,
            context = context,
            onSuccess = {
                Toast.makeText(context, "Profile updated successfully!", Toast.LENGTH_SHORT).show()
                navController.popBackStack()  // Go back to Profile screen
            },
            onFailure = { error ->
                Toast.makeText(context, "Error updating profile: $error", Toast.LENGTH_SHORT).show()
            }
        )
    }

    // Layout to edit profile information
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Edit Profile") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Rounded.Menu, contentDescription = "Back")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { updateProfile() }) {
                Icon(Icons.Filled.Add, contentDescription = "Save Changes")
            }
        }
    ) { paddingValue ->
        Column(modifier = modifier
            .padding(paddingValue)
            .padding(16.dp)) {
            TextField(value = name, onValueChange = { name = it }, label = { Text("Name") })
            Spacer(modifier = Modifier.height(8.dp))
            TextField(
                value = shopName,
                onValueChange = { shopName = it },
                label = { Text("Shop Name") })
            Spacer(modifier = Modifier.height(8.dp))
            TextField(
                value = phoneNumber,
                onValueChange = { phoneNumber = it },
                label = { Text("Phone Number") })
            Spacer(modifier = Modifier.height(8.dp))
            TextField(
                value = address,
                onValueChange = { address = it },
                label = { Text("Address") })
        }
    }
}
