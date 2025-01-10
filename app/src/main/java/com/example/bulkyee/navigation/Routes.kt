package com.example.bulkyee.navigation

sealed class Routes(val routes: String) {
    object SplashScreen : Routes("SplashScreen")
    object LoginScreen : Routes("LoginScreen")
    object InformationScreen : Routes("InformationScreen")
    object HomeScreen : Routes("HomeScreen")
    object ProfileScreen : Routes("ProfileScreen")
    object SearchScreen : Routes("SearchScreen")
    object SettingScreen : Routes("SettingScreen")
    object AllOrdersScreen : Routes("AllOrdersScreen")
    object OrderPaymentScreen : Routes("OrderPaymentScreen")
    object EditProfileScreen : Routes("EditProfileScreen")
    object MyOrderScreen : Routes("MyOrderScreen")
    object CheckOutScreen : Routes("checkout_screen?cart={cartQueryParam}")
    object AddressAndPaymentScreen : Routes("address_and_payment_screen/{cartQueryParam}")
    object EditItemScreen : Routes("EditItemScreen/{EditItemId}")
    object OrderScreen : Routes("OrderScreen")
    object OrderDetail : Routes("OrderDetail/{OrderId}")
    object AddItemScreen : Routes("AddItemScreen")

}