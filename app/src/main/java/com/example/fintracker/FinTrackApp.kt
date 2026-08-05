package com.example.fintracker

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.List
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.feature.accounts.presentation.AccountScreen
import com.example.feature.accounts.presentation.AccountScreenPreview
import com.example.feature.transactions.presentation.TransactionScreen


sealed class FinTrackDestination(val route: String, val label: String, val icon: ImageVector) {
    data object Transactions :
        FinTrackDestination("transactions", "Transactions", Icons.Default.List)

    data object Accounts : FinTrackDestination("accounts", "Accounts", Icons.Default.AccountBalance)
}

val bottomNavDestinations = listOf(FinTrackDestination.Transactions, FinTrackDestination.Accounts)

@Composable
fun FinTrackApp() {
    val navController = rememberNavController()

    Scaffold(
        bottomBar = {
            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val currentRoute = navBackStackEntry?.destination?.route

            NavigationBar {
                bottomNavDestinations.forEach { destination ->
                    NavigationBarItem(
                        selected = currentRoute == destination.route,
                        onClick = {
                            navController.navigate(destination.route) {
                                popUpTo(navController.graph.startDestinationId) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = {
                            Icon(
                                destination.icon, contentDescription = destination.label
                            )
                        },
                        label = { Text(destination.label) }
                    )
                }
            }
        }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = FinTrackDestination.Transactions.route,
            modifier = Modifier.padding(paddingValues)
        ) {
            composable(FinTrackDestination.Transactions.route){
                TransactionScreen(showDebugControls = BuildConfig.DEBUG)
            }
            composable(FinTrackDestination.Accounts.route) {
                AccountScreen()
            }
        }
    }

}