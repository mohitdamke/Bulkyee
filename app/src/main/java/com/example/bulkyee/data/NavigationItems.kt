package com.example.bulkyee.data

import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavController

data class NavigationItems(
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val badgeCount: Int? = null,
    val route: String
)