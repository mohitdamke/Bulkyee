package com.store.mybulkyee.navigation

sealed class Routes(val routes: String) {
    object SplashScreen : Routes("SplashScreen")
    object LoginScreen : Routes("LoginScreen")
    object InformationScreen : Routes("InformationScreen")
    object HomeScreen : Routes("HomeScreen")
    object ProfileScreen : Routes("ProfileScreen")
    object SearchScreen : Routes("SearchScreen")
    object PrivacyPolicy : Routes("PrivacyPolicy")
    object ContactUsScreen : Routes("ContactUsScreen")
    object EditProfileScreen : Routes("EditProfileScreen")
    object MyOrderScreen : Routes("MyOrderScreen")
    object CheckOutScreen : Routes("checkout_screen?cart={cartQueryParam}")


}