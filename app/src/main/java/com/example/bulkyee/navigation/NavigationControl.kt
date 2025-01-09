package com.example.bulkyee.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.bulkyee.screens.HomeScreen
import com.example.bulkyee.screens.InformationScreen
import com.example.bulkyee.screens.LoginScreen
import com.example.bulkyee.screens.OrderPaymentScreen
import com.example.bulkyee.screens.SplashScreen

@Composable
fun NavigationControl() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = Routes.HomeScreen.routes) {

        composable(route = Routes.SplashScreen.routes) {
            SplashScreen(navController = navController)
        }
        composable(route = Routes.LoginScreen.routes) {
            LoginScreen(navController = navController)
        }
        composable(route = Routes.InformationScreen.routes) {
            InformationScreen(navController = navController)
        }
        composable(route = Routes.HomeScreen.routes) {
            HomeScreen(navController = navController)
        }
        composable(route = Routes.OrderPaymentScreen.routes) {
            OrderPaymentScreen(navController = navController)
        }
//        composable(route = Routes.SearchScreen.routes) {
//            SearchScreen(navController = navController)
//        }
//        composable(route = Routes.AddItemScreen.routes) {
//            AddItemScreen(navController = navController)
//        }
//        composable(route = Routes.OrderScreen.routes) {
//            OrderScreen(navController = navController)
//        }
//        composable(route = Routes.OrderDetail.routes) {
//            OrderDetail(navController = navController)
//        }
//        composable(route = Routes.EditItemScreen.routes) {
//            val itemId = it.arguments!!.getString("EditItemId")
//            itemId?.let {
//                EditItemScreen(navController = navController, itemId = itemId)
//            }
//        }


    }
}