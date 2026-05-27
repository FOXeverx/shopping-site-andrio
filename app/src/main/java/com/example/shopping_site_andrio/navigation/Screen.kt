package com.example.shopping_site_andrio.navigation

sealed class Screen(val route: String) {
    data object Login : Screen("login")
    data object Home : Screen("home")
    data object Cart : Screen("cart")
    data object Profile : Screen("profile")
    data object OrderList : Screen("order_list")
    data object OrderDetail : Screen("order_detail/{id}") {
        fun createRoute(id: Int) = "order_detail/$id"
    }
    data object ProductDetail : Screen("product_detail/{id}") {
        fun createRoute(id: Int) = "product_detail/$id"
    }
}
