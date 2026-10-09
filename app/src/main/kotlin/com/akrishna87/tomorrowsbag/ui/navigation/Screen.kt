package com.akrishna87.tomorrowsbag.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Person
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(val route: String, val label: String, val icon: ImageVector) {
    data object Kid : Screen("kid", "Kid", Icons.Filled.Face)
    data object Parent : Screen("parent", "Parent", Icons.Filled.Person)

    companion object {
        val bottomNavItems = listOf(Kid, Parent)
    }
}
