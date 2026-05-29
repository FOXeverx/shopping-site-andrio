package com.example.shopping_site_andrio

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.ShoppingCart
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
    val darkMode by loginViewModel.darkMode.collectAsState(initial = "system")

    val startDestination = if (loginState.isLoggedIn) Screen.Home.route else Screen.Login.route

    val effectiveDarkMode = when (darkMode) {
        "dark" -> true
        "light" -> false
        else -> null
    }

    val wasLoggedIn = remember { mutableStateOf(loginState.isLoggedIn) }
    LaunchedEffect(loginState.isLoggedIn) {
        if (wasLoggedIn.value && !loginState.isLoggedIn) {
            navController.navigate(Screen.Login.route) {
                popUpTo(0) { inclusive = true }
            }
        }
        wasLoggedIn.value = loginState.isLoggedIn
    }

    val bottomNavRoutes = listOf(
        Screen.Home.route,
        Screen.Cart.route,
        Screen.OrderList.route,
        Screen.Profile.route
    )
    val showBottomBar = currentRoute in bottomNavRoutes

    Shopping_Site_AndrioTheme(darkModeOverride = effectiveDarkMode) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            bottomBar = {
                if (showBottomBar) {
                    NavigationBar {
                        NavigationBarItem(
                            icon = { Icon(Icons.Filled.Home, contentDescription = "Home") },
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
                            icon = { Icon(Icons.Filled.ShoppingCart, contentDescription = "Cart") },
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
                            icon = { Icon(Icons.Filled.Receipt, contentDescription = "Orders") },
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
                            icon = { Icon(Icons.Filled.Person, contentDescription = "Profile") },
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
}
