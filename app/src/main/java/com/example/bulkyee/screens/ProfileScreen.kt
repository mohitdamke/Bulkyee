package com.example.bulkyee.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.bulkyee.dimensions.FamilyDim
import com.example.bulkyee.dimensions.FontDim
import com.example.bulkyee.navigation.Routes
import com.example.bulkyee.ui.theme.Brown40
import com.example.bulkyee.ui.theme.White10
import com.example.bulkyee.viewmodel.ProfileViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(modifier: Modifier = Modifier, navController: NavController) {

    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())

    val context = LocalContext.current
    val profileViewModel: ProfileViewModel = viewModel()
    var name by remember { mutableStateOf("") }
    var shopName by remember { mutableStateOf("") }
    var phoneNumber by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }

    // Fetch user details from Firestore
    LaunchedEffect(true) {
        val userProfile = profileViewModel.fetchUserProfile()
        name = userProfile["name"] ?: ""
        shopName = userProfile["shopName"] ?: ""
        phoneNumber = userProfile["phoneNumber"] ?: ""
        address = userProfile["address"] ?: ""
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
                        text = "Profile",
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
                actions = {
                    Icon(
                        Icons.Rounded.Edit,
                        contentDescription = "Edit Profile",
                        modifier = Modifier
                            .size(30.dp)
                            .clickable { navController.navigate(Routes.EditProfileScreen.routes) },
                        tint = Brown40
                    )
                },
                navigationIcon = {
                    Icon(
                        Icons.Rounded.ArrowBack,
                        contentDescription = "",
                        modifier = Modifier
                            .size(30.dp)
                            .clickable { navController.navigate(Routes.HomeScreen.routes) },
                        tint = Brown40
                    )
                    Spacer(
                        modifier = Modifier.padding(start = 10.dp)
                    )
                }

            )
        },
    ) { paddingValues ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .background(White10)
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            ProfileDetail("Name", name)
            ProfileDetail("Shop Name", shopName)
            ProfileDetail("Phone Number", phoneNumber)
            ProfileDetail("Address", address)
        }
    }
}


@Composable
fun ProfileDetail(label: String, value: String) {
    Row(modifier = Modifier.padding(vertical = 8.dp)) {
        Text(
            text = "$label: ",
            fontSize = FontDim.mediumTextSize,
            fontFamily = FamilyDim.Normal,
            color = Brown40,
        )
        Text(
            text = value, fontSize = FontDim.mediumTextSize,
            fontFamily = FamilyDim.Normal,
            color = Color.Black
        )
    }
}