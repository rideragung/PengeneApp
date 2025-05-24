package com.example.pengene.ui.navigation

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Register : Screen("register")
    object Wishlist : Screen("wishlist")
    object AddWishlist : Screen("add_wishlist")
    object EditWishlist : Screen("edit_wishlist/{itemId}") {
        fun createRoute(itemId: String) = "edit_wishlist/$itemId"
    }
}