package com.example.mybulkyee.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.mybulkyee.screens.CheckOutScreen
import com.example.mybulkyee.screens.ContactUsScreen
import com.example.mybulkyee.screens.EditProfileScreen
import com.example.mybulkyee.screens.HomeScreen
import com.example.mybulkyee.screens.InformationScreen
import com.example.mybulkyee.screens.LoginScreen
import com.example.mybulkyee.screens.MyOrderScreen
import com.example.mybulkyee.screens.PrivacyPolicyScreen
import com.example.mybulkyee.screens.ProfileScreen
import com.example.mybulkyee.screens.SearchScreen
import com.example.mybulkyee.screens.SplashScreen

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
        composable(route = Routes.PrivacyPolicy.routes) {
            PrivacyPolicyScreen(navController = navController)
        }
        composable(route = Routes.ContactUsScreen.routes) {
            ContactUsScreen(navController = navController)
        }
        composable(route = Routes.MyOrderScreen.routes) {
            MyOrderScreen(navController = navController)
        }
        composable(route = Routes.EditProfileScreen.routes) {
            EditProfileScreen(navController = navController)
        }
        composable(
            route = Routes.CheckOutScreen.routes,
            arguments = listOf(navArgument("cartQueryParam") { type = NavType.StringType })
        ) { backStackEntry ->
            val cartQueryParam = backStackEntry.arguments?.getString("cartQueryParam")
            CheckOutScreen(navController = navController, cartQueryParam = cartQueryParam)
        }


    }
}