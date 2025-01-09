package com.example.bulkyee.navigation

sealed class  Routes(val routes: String) {
    object SplashScreen : Routes("SplashScreen")
    object LoginScreen : Routes("LoginScreen")
    object InformationScreen : Routes("InformationScreen")
    object HomeScreen : Routes("HomeScreen")
    object OrderPaymentScreen : Routes("OrderPaymentScreen")
    object EditItemScreen : Routes("EditItemScreen/{EditItemId}")
    object OrderScreen : Routes("OrderScreen")
    object OrderDetail : Routes("OrderDetail/{OrderId}")
    object AddItemScreen : Routes("AddItemScreen")
    object SearchScreen : Routes("SearchScreen")

}