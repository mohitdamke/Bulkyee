package com.example.bulkyee.navigation

sealed class Routes(val routes: String) {
    object SplashScreen : Routes("SplashScreen")
    object LoginScreen : Routes("LoginScreen")
    object InformationScreen : Routes("InformationScreen")
    object HomeScreen : Routes("HomeScreen")
    object ProfileScreen : Routes("ProfileScreen")
    object SettingScreen : Routes("SettingScreen")
    object AllOrdersScreen : Routes("AllOrdersScreen")
    object OrderPaymentScreen : Routes("OrderPaymentScreen")
    object EditProfileScreen : Routes("EditProfileScreen")
    object CheckOutScreen : Routes("checkout_screen?cart={cartQueryParam}")
    object EditItemScreen : Routes("EditItemScreen/{EditItemId}")
    object OrderScreen : Routes("OrderScreen")
    object OrderDetail : Routes("OrderDetail/{OrderId}")
    object AddItemScreen : Routes("AddItemScreen")
    object SearchScreen : Routes("SearchScreen")

}