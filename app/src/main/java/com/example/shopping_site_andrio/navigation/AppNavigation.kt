package com.example.shopping_site_andrio.navigation

import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavDeepLink
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navDeepLink
import com.example.shopping_site_andrio.ui.screen.cart.CartScreen
import com.example.shopping_site_andrio.ui.screen.detail.ProductDetailScreen
import com.example.shopping_site_andrio.ui.screen.home.HomeScreen
import com.example.shopping_site_andrio.ui.screen.login.LoginScreen
import com.example.shopping_site_andrio.ui.screen.order.OrderDetailScreen
import com.example.shopping_site_andrio.ui.screen.order.OrderListScreen
import com.example.shopping_site_andrio.ui.screen.profile.ProfileScreen
import com.example.shopping_site_andrio.ui.screen.admin.*

@Composable
fun AppNavigation(
    navController: NavHostController,
    isLoggedIn: Boolean,
    startDestination: String,
    modifier: Modifier = Modifier
) {
    val enterAnim = fadeIn(animationSpec = tween(300)) + slideInHorizontally(animationSpec = tween(300)) { it / 4 }
    val exitAnim = fadeOut(animationSpec = tween(300)) + slideOutHorizontally(animationSpec = tween(300)) { -it / 4 }

    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        composable(Screen.Login.route) {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                }
            )
        }
        composable(
            route = Screen.Home.route,
            enterTransition = { enterAnim },
            exitTransition = { exitAnim }
        ) {
            HomeScreen(
                onProductClick = { productId ->
                    navController.navigate(Screen.ProductDetail.createRoute(productId))
                }
            )
        }
        composable(
            route = Screen.ProductDetail.route,
            deepLinks = listOf(
                navDeepLink { uriPattern = "https://shop.example.com/product/{id}" }
            ),
            arguments = listOf(navArgument("id") { type = NavType.IntType }),
            enterTransition = { enterAnim },
            exitTransition = { exitAnim }
        ) { backStackEntry ->
            val productId = backStackEntry.arguments?.getInt("id") ?: return@composable
            ProductDetailScreen(
                productId = productId,
                onBack = { navController.popBackStack() },
                onProductClick = { id ->
                    navController.navigate(Screen.ProductDetail.createRoute(id))
                }
            )
        }
        composable(
            route = Screen.Cart.route,
            enterTransition = { enterAnim },
            exitTransition = { exitAnim }
        ) {
            CartScreen()
        }
        composable(
            route = Screen.Profile.route,
            enterTransition = { enterAnim },
            exitTransition = { exitAnim }
        ) {
            ProfileScreen(
                onLogout = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(0) { inclusive = true }
                    }
                },
                onAdminClick = if (isLoggedIn) {
                    { navController.navigate(Screen.AdminDashboard.route) }
                } else null
            )
        }
        composable(
            route = Screen.OrderList.route,
            enterTransition = { enterAnim },
            exitTransition = { exitAnim }
        ) {
            OrderListScreen(
                onOrderClick = { orderId ->
                    navController.navigate(Screen.OrderDetail.createRoute(orderId))
                }
            )
        }
        composable(
            route = Screen.OrderDetail.route,
            arguments = listOf(navArgument("id") { type = NavType.IntType }),
            enterTransition = { enterAnim },
            exitTransition = { exitAnim }
        ) { backStackEntry ->
            val orderId = backStackEntry.arguments?.getInt("id") ?: return@composable
            OrderDetailScreen(
                orderId = orderId,
                onBack = { navController.popBackStack() }
            )
        }

        composable(Screen.AdminDashboard.route) {
            AdminDashboardScreen(
                onBack = { navController.popBackStack() },
                onNavigateToUsers = { navController.navigate(Screen.AdminUsers.route) },
                onNavigateToLogs = { navController.navigate(Screen.AdminLogs.route) },
                onNavigateToAnomalies = { navController.navigate(Screen.AdminAnomalies.route) },
                onNavigateToSecurity = { navController.navigate(Screen.AdminSecurity.route) }
            )
        }
        composable(Screen.AdminUsers.route) {
            AdminUsersScreen(
                onBack = { navController.popBackStack() },
                onUserClick = { userId -> navController.navigate(Screen.AdminUserDetail.createRoute(userId)) }
            )
        }
        composable(
            route = Screen.AdminUserDetail.route,
            arguments = listOf(navArgument("id") { type = NavType.IntType })
        ) { backStackEntry ->
            val userId = backStackEntry.arguments?.getInt("id") ?: return@composable
            AdminUserDetailScreen(userId = userId, onBack = { navController.popBackStack() })
        }
        composable(Screen.AdminLogs.route) {
            AdminLogsScreen(onBack = { navController.popBackStack() })
        }
        composable(Screen.AdminAnomalies.route) {
            AdminAnomaliesScreen(onBack = { navController.popBackStack() })
        }
        composable(Screen.AdminSecurity.route) {
            AdminSecurityScreen(onBack = { navController.popBackStack() })
        }
    }
}
