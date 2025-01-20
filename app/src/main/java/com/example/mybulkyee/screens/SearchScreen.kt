package com.example.mybulkyee.screens

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.mybulkyee.dimensions.FamilyDim
import com.example.mybulkyee.dimensions.FontDim

@Composable
fun SearchScreen(navController: NavController) {
//
//    val context = LocalContext.current
//    val searchViewModel: SearchViewModel = viewModel()
//    val searchResults by searchViewModel.searchResults.collectAsState()
//    val isLoading by searchViewModel.isLoading.collectAsState()
//    val errorMessage by searchViewModel.errorMessage.collectAsState()
//    var cart by remember { mutableStateOf(listOf<Item>()) }  // Cart state
//
//    var query by remember { mutableStateOf("") }
//    // Add item to cart
//    fun addToCart(item: Item) {
//        cart = cart.toMutableList().apply {
//            val index = indexOfFirst { it.itemId == item.itemId }
//            if (index != -1) {
//                this[index] = item  // Update quantity if item exists in cart
//            } else {
//                add(item)  // Add new item if it doesn't exist in cart
//            }
//        }
//    }
//
//    // Remove item from cart
//    fun removeFromCart(item: Item) {
//        cart = cart.toMutableList().apply {
//            remove(item)  // Remove item from cart
//        }
//    }
//    Scaffold(modifier = Modifier.fillMaxSize()) { paddingValues ->
//
//        Column(
//            modifier = Modifier
//                .fillMaxSize()
//                .padding(paddingValues)
//                .padding(10.dp)
//        ) {
//
//            SearchOutlineText(
//                value = query,
//                onValueChange = {
//                    query = it
//                    searchViewModel.searchProducts(it)
//                    // Trigger search as query changes //
//                },
//                label = "Search",
//                icons = Icons.Default.Search
//            )
//
//            Spacer(modifier = Modifier.height(16.dp))
//
//            // Show loading indicator when loading
//            if (isLoading) {
//                CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
//            }
//
//            // Show error message if there is an error
//            if (errorMessage != null) {
//                Text(
//                    text = errorMessage ?: "",
//                    color = Color.Red,
//                    modifier = Modifier.padding(16.dp)
//                )
//            }
//
//            // Check if there are no results and show "No results found" message
//            if (searchResults.isEmpty() && !isLoading) {
//                Text(
//                    text = "No search results found.",
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .padding(16.dp),
//                    color = Color.Gray,
//                )
//            } else {
//
//                // Display search results
//                LazyColumn(modifier = Modifier.fillMaxSize()) {
//                    items(searchResults) { item ->
//                        ItemCard(
//                            item = item,
//                            navController = navController,
//                            context = context,
//                            onAddToCart = { addToCart(it) },
//                            onRemoveFromCart = { removeFromCart(it) },
//                            cart = cart,
//                        )                    }
//                }
//            }
//        }
//    }
}



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchOutlineText(
    modifier: Modifier = Modifier,
    value: String,
    icons: ImageVector,
    onValueChange: (String) -> Unit,
    label: String
) {
    OutlinedTextField(
        value = value.trim(),
        onValueChange = { onValueChange(it) },
        singleLine = true,
        leadingIcon = {
            Icon(
                imageVector = icons,
                contentDescription = "",
                modifier = Modifier
                    .size(30.dp),
                tint = Color.LightGray

            )
        },
        placeholder = {
            Text(
                text = "Type your $label",
                fontSize = FontDim.mediumTextSize,
                fontFamily = FamilyDim.Medium,
                color = Color.Gray,
                maxLines = 1,
                overflow = TextOverflow.Visible
            )
        },
        colors = TextFieldDefaults.outlinedTextFieldColors(
            unfocusedPlaceholderColor = Color.Gray,
            focusedPlaceholderColor = Color.Gray,
            focusedBorderColor = Color.Gray,
            unfocusedBorderColor = Color.Gray,
            focusedTextColor = Color.Black,
            unfocusedTextColor = Color.Black
        ),
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Text, imeAction = ImeAction.Search
        ),
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(100.dp),
        minLines = 1
    )
}