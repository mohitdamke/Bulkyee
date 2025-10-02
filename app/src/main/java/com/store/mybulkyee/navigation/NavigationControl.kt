package com.store.mybulkyee.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.store.mybulkyee.screens.CheckOutScreen
import com.store.mybulkyee.screens.ContactUsScreen
import com.store.mybulkyee.screens.EditProfileScreen
import com.store.mybulkyee.screens.HomeScreen
import com.store.mybulkyee.screens.InformationScreen
import com.store.mybulkyee.screens.LoginScreen
import com.store.mybulkyee.screens.MyOrderScreen
import com.store.mybulkyee.screens.PrivacyPolicyScreen
import com.store.mybulkyee.screens.ProfileScreen
import com.store.mybulkyee.screens.SearchScreen
import com.store.mybulkyee.screens.SplashScreen

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