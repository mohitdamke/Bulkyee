package com.example.mybulkyee.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Black
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.mybulkyee.dimensions.FamilyDim
import com.example.mybulkyee.dimensions.FontDim
import com.example.mybulkyee.navigation.Routes
import com.example.mybulkyee.ui.theme.Brown40
import com.example.mybulkyee.ui.theme.White10

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContactUsScreen(modifier: Modifier = Modifier, navController: NavController) {

    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())
    val context = LocalContext.current
    val scope = rememberCoroutineScope()


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
                        text = "Contact Us",
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
            modifier = Modifier
                .fillMaxSize()
                .background(White10)
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Display Name
            Text(
                text = "Name: Dhiraj Damke",
                fontSize = 18.sp,
                color = Black,
                modifier = Modifier.padding(8.dp)
            )

            // Display Address
            Text(
                text = "Address: Plot no 4 New Prerna Nagar, Nagpur, Maharashtra, 440034",
                fontSize = 18.sp,
                color = Black,
                modifier = Modifier.padding(8.dp),
                textAlign = TextAlign.Center
            )

            // Display Contact Number
            Text(
                text = "Contact: +(91 9881199408)",
                fontSize = 18.sp,
                color = Black,
                modifier = Modifier.padding(8.dp)
            )
            // Display Contact Number
            Text(
                text = "Email: dhirajenterprises13@gmail.com",
                fontSize = 18.sp,
                color = Black,
                modifier = Modifier.padding(8.dp)
            )
        }
    }
}