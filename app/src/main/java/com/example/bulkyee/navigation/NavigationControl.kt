package com.example.bulkyee.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.bulkyee.screens.AddressAndPaymentScreen
import com.example.bulkyee.screens.CheckOutScreen
import com.example.bulkyee.screens.EditProfileScreen
import com.example.bulkyee.screens.HomeScreen
import com.example.bulkyee.screens.InformationScreen
import com.example.bulkyee.screens.LoginScreen
import com.example.bulkyee.screens.MyOrderScreen
import com.example.bulkyee.screens.ProfileScreen
import com.example.bulkyee.screens.SearchScreen
import com.example.bulkyee.screens.SettingScreen
import com.example.bulkyee.screens.SplashScreen

@Composable
fun NavigationControl() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = Routes.SplashScreen.routes) {

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
        composable(route = Routes.SearchScreen.routes) {
            SearchScreen(navController = navController)
        }
        composable(route = Routes.ProfileScreen.routes) {
            ProfileScreen(navController = navController)
        }
        composable(route = Routes.SettingScreen.routes) {
            SettingScreen(navController = navController)
        }
        composable(route = Routes.MyOrderScreen.routes) {
            MyOrderScreen(navController = navController)
        }
        composable(route = Routes.EditProfileScreen.routes) {
            EditProfileScreen(navController = navController)
        }
        composable(
            route = "checkout_screen?cart={cartQueryParam}",
            arguments = listOf(navArgument("cartQueryParam") { type = NavType.StringType })
        ) { backStackEntry ->
            val cartQueryParam = backStackEntry.arguments?.getString("cartQueryParam")
            CheckOutScreen(navController = navController, cartQueryParam = cartQueryParam)
        }

        composable(
            route = Routes.AddressAndPaymentScreen.routes,
            arguments = listOf(navArgument("cartQueryParam") { type = NavType.StringType })
        ) {
            val cartQueryParam = it.arguments?.getString("cartQueryParam") ?: ""
            AddressAndPaymentScreen(navController = navController, cartQueryParam = cartQueryParam)
        }


    }
}