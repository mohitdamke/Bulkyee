package com.example.mybulkyee.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Shop
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
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
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.mybulkyee.dimensions.FamilyDim
import com.example.mybulkyee.dimensions.FontDim
import com.example.mybulkyee.ui.theme.Brown40
import com.example.mybulkyee.ui.theme.White10
import com.example.mybulkyee.viewmodel.ProfileViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileScreen(modifier: Modifier = Modifier, navController: NavController) {

    val profileViewModel: ProfileViewModel = hiltViewModel()
    var name by remember { mutableStateOf("") }
    var shopName by remember { mutableStateOf("") }
    var phoneNumber by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())
    val userProfile by profileViewModel.userProfile.collectAsState()

    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val firebaseUser = FirebaseAuth.getInstance().currentUser
    val userId = firebaseUser?.uid ?: return // Exit if user is null
    val db = FirebaseFirestore.getInstance()

    // Fetch profile data when the screen loads
    LaunchedEffect(Unit) {
        profileViewModel.fetchUserProfile()
    }

    // Fetch existing profile data
    LaunchedEffect(userProfile) {
        userProfile?.let {
            name = it["name"] ?: ""
            shopName = it["shopName"] ?: ""
            phoneNumber = it["phoneNumber"] ?: ""
            address = it["address"] ?: ""
        }
    }


    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .background(White10)
            .nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            CenterAlignedTopAppBar(
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = White10,
                    actionIconContentColor = Color.White,
                    navigationIconContentColor = Color.White,
                    scrolledContainerColor = Color.Black,
                ),
                title = {
                    Text(
                        text = "Edit Profile",
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
                navigationIcon = {
                    Icon(
                        Icons.Rounded.ArrowBack,
                        contentDescription = "Back",
                        modifier = Modifier
                            .size(30.dp)
                            .clickable {
                                scope.launch {
                                    navController.navigateUp()
                                }
                            },
                        tint = Brown40
                    )
                },
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = {
                    if (name.isBlank() || shopName.isBlank() || phoneNumber.isBlank() || address.isBlank()) {
                        Toast.makeText(
                            context,
                            "Please fill all fields!",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else if (!phoneNumber.matches("\\d{10}".toRegex())) {
                        Toast.makeText(
                            context,
                            "Enter a valid 10-digit phone number!",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        scope.launch {
                            profileViewModel.updateProfile(
                                name = name,
                                shopName = shopName,
                                phoneNumber = phoneNumber,
                                address = address,
                                onSuccess = {
                                    Toast.makeText(
                                        context,
                                        "Profile updated successfully!",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    navController.popBackStack()  // Go back to Profile screen
                                },

                            )
                        }
                    }
                },
                containerColor = Brown40,
                contentColor = Color.White,
                icon = { Icon(Icons.Filled.DoneAll, contentDescription = "Done.") },
                text = {
                    Text(
                        text = "Update",
                        fontSize = FontDim.mediumTextSize,
                        fontFamily = FamilyDim.Normal
                    )
                }
            )
        },
    ) { paddingValue ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .background(White10)
                .padding(paddingValue)
                .padding(10.dp)
        ) {
            EditProfileOutlineText(
                value = name,
                onValueChange = { name = it },
                label = "Name",
                icons = Icons.Default.Person,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text, imeAction = ImeAction.Next
                )
            )
            Spacer(modifier = Modifier.height(8.dp))
            EditProfileOutlineText(
                value = shopName,
                onValueChange = { shopName = it },
                label = "Shop Name",
                icons = Icons.Default.Shop,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text, imeAction = ImeAction.Next
                )
            )
            Spacer(modifier = Modifier.height(8.dp))
            EditProfileOutlineText(
                value = phoneNumber,
                onValueChange = { phoneNumber = it },
                label = "Phone No",
                icons = Icons.Default.Phone,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number, imeAction = ImeAction.Next
                )
            )
            Spacer(modifier = Modifier.height(8.dp))
            EditProfileOutlineText(
                value = address,
                onValueChange = { address = it },
                label = "Address",
                icons = Icons.Default.LocationOn,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text, imeAction = ImeAction.Done
                )
            )
            Spacer(modifier = Modifier.height(8.dp))

        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EditProfileOutlineText(
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
        colors = TextFieldDefaults.outlinedTextFieldColors(
            unfocusedPlaceholderColor = Color.Gray,
            focusedPlaceholderColor = Color.Gray,
            focusedBorderColor = Brown40,
            unfocusedBorderColor = Brown40,
            focusedTextColor = Color.Black,
            unfocusedTextColor = Color.Black
        ),
        keyboardOptions = keyboardOptions,
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(100.dp),
        minLines = 1
    )
}