package com.example.bulkyee.screens

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.rounded.MoreVert
import androidx.compose.material.icons.rounded.Person2
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Black
import androidx.compose.ui.graphics.Color.Companion.Gray
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.bulkyee.R
import com.example.bulkyee.data.Item
import com.example.bulkyee.dimensions.FamilyDim
import com.example.bulkyee.dimensions.FontDim
import com.example.bulkyee.navigation.Routes
import com.example.bulkyee.viewmodel.HomeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(modifier: Modifier = Modifier, navController: NavController) {
    val context = LocalContext.current
    val homeViewModel: HomeViewModel = viewModel()

    // Pull-to-refresh state
    val isLoading by homeViewModel.isLoading.collectAsState(false)
    val isSuccess by homeViewModel.isSuccess.collectAsState(false)
    val isError by homeViewModel.isError.collectAsState(false)
    val items = homeViewModel.items.collectAsState().value
    var totalAmount by remember { mutableIntStateOf(0) }  // Track total amount

    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())

    LaunchedEffect(key1 = homeViewModel) {
        homeViewModel.fetchItems()
    }

    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Black,
                    titleContentColor = White,
                    actionIconContentColor = White,
                    navigationIconContentColor = White,
                    scrolledContainerColor = Black,
                ),
                title = {
                    Text(
                        text = "Bulkyee",
                        maxLines = 1,
                        letterSpacing = 1.sp,
                        textAlign = TextAlign.Center,
                        fontSize = FontDim.extraLargeTextSize,
                        overflow = TextOverflow.Visible,
                        fontFamily = FamilyDim.Bold,
                    )
                },
                scrollBehavior = scrollBehavior,
                actions = {
                    Icon(
                        Icons.Rounded.Search,
                        contentDescription = "",
                        modifier = Modifier
                            .size(30.dp)
                            .clickable { navController.navigate(Routes.SearchScreen.routes) },
                        tint = White
                    )
                    Spacer(modifier = Modifier.padding(10.dp))
                    Icon(
                        Icons.Rounded.Person2,
                        contentDescription = "",
                        modifier = Modifier
                            .size(30.dp)
                            .clickable { navController.navigate(Routes.OrderScreen.routes) },
                        tint = White
                    )
                },
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate(Routes.OrderPaymentScreen.routes) },
                containerColor = Black,
                contentColor = White,
            ) {
                Icon(Icons.Filled.Add, contentDescription = "View Cart")
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(10.dp)
        ) {
            if (isLoading) {
                // Show loading indicator
                CircularProgressIndicator(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .size(50.dp)
                ) // Adjust the size to prevent it from taking full size)
            }
            if (isError && items.isEmpty()) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(10.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    // Show error message
                    Text(
                        text = "An error occurred while adding the item. Please try again.",
                        color = Color.Red,
                        fontSize = 18.sp,
                        modifier = Modifier.padding(16.dp)
                    )}
            }
        }

        // Show loading or no items message based on the state of the items list
        if (isSuccess && items.isEmpty()) {

            // Show "No items available" message
            // Show a message indicating no items are available
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(10.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {

                Text(
                    text = "No items available.",
                    fontSize = FontDim.mediumTextSize,
                    fontFamily = FamilyDim.Bold,
                    color = Black
                )
            }
        } else if (isSuccess) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp)
            ) {
                items(items) { item ->
                    ItemCard(item = item, navController = navController, context = context)
                }
            }
        }
    }
}

@Composable
fun ItemCard(context: Context, item: Item, navController: NavController) {

//    val decodedImage = decodeBase64ToBitmap(item.imageUrl)
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter =
//                if (decodedImage != null) {
//                    rememberAsyncImagePainter(decodedImage)  // Use the decoded Bitmap
//                } else {
                    painterResource(id = R.drawable.ic_launcher_background)  // Placeholder image
                ,
                contentDescription = "Item Image",
                modifier = Modifier
                    .clip(CircleShape)
                    .size(80.dp),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.width(10.dp))
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = item.itemName,
                    fontSize = FontDim.mediumTextSize,
                    fontFamily = FamilyDim.Bold
                )
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    Text(
                        text = "₹${item.realPrice}",
                        fontSize = FontDim.mediumTextSize,
                        fontFamily = FamilyDim.Bold,
                        modifier = Modifier.padding(end = 10.dp)
                    )
                    Text(
                        text = "₹${item.discountedPrice}",
                        fontSize = FontDim.mediumTextSize,
                        fontFamily = FamilyDim.Bold,
                        textDecoration = TextDecoration.LineThrough,
                        color = Gray
                    )
                }
            }
            Icon(
                imageVector = Icons.Rounded.MoreVert,
                contentDescription = "",
                modifier = Modifier
                    .size(30.dp)
                    .clickable {

                    },
                tint = Gray
            )
        }
    }


}
