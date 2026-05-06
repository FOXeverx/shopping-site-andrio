package com.example.shopping_site_andrio

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.shopping_site_andrio.navigation.AppNavigation
import com.example.shopping_site_andrio.navigation.Screen
import com.example.shopping_site_andrio.ui.screen.login.LoginViewModel
import com.example.shopping_site_andrio.ui.theme.Shopping_Site_AndrioTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Shopping_Site_AndrioTheme {
                MainApp()
            }
        }
    }
}

@Composable
fun MainApp() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val loginViewModel: LoginViewModel = hiltViewModel()
    val loginState by loginViewModel.uiState.collectAsState()

    val startDestination = if (loginState.isLoggedIn) Screen.Home.route else Screen.Login.route

    val bottomNavRoutes = listOf(
        Screen.Home.route,
        Screen.Cart.route,
        Screen.OrderList.route,
        Screen.Profile.route
    )
    val showBottomBar = currentRoute in bottomNavRoutes

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            if (showBottomBar) {
                NavigationBar {
                    NavigationBarItem(
                        icon = { Text("H") },
                        label = { Text("Home") },
                        selected = currentRoute == Screen.Home.route,
                        onClick = {
                            if (currentRoute != Screen.Home.route) {
                                navController.navigate(Screen.Home.route) {
                                    popUpTo(Screen.Home.route) { inclusive = true }
                                }
                            }
                        }
                    )
                    NavigationBarItem(
                        icon = { Text("C") },
                        label = { Text("Cart") },
                        selected = currentRoute == Screen.Cart.route,
                        onClick = {
                            if (currentRoute != Screen.Cart.route) {
                                navController.navigate(Screen.Cart.route) {
                                    popUpTo(Screen.Home.route)
                                }
                            }
                        }
                    )
                    NavigationBarItem(
                        icon = { Text("O") },
                        label = { Text("Orders") },
                        selected = currentRoute == Screen.OrderList.route,
                        onClick = {
                            if (currentRoute != Screen.OrderList.route) {
                                navController.navigate(Screen.OrderList.route) {
                                    popUpTo(Screen.Home.route)
                                }
                            }
                        }
                    )
                    NavigationBarItem(
                        icon = { Text("P") },
                        label = { Text("Profile") },
                        selected = currentRoute == Screen.Profile.route,
                        onClick = {
                            if (currentRoute != Screen.Profile.route) {
                                navController.navigate(Screen.Profile.route) {
                                    popUpTo(Screen.Home.route)
                                }
                            }
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        AppNavigation(
            navController = navController,
            isLoggedIn = loginState.isLoggedIn,
            startDestination = startDestination,
            modifier = Modifier.padding(innerPadding)
        )
    }
}
