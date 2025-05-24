package com.example.pengene.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.NavType
import com.example.pengene.domain.model.AuthState
import com.example.pengene.ui.screens.auth.AuthViewModel
import com.example.pengene.ui.screens.auth.LoginScreen
import com.example.pengene.ui.screens.auth.RegisterScreen
import com.example.pengene.ui.screens.wishlist.AddEditWishlistScreen
import com.example.pengene.ui.screens.wishlist.WishlistScreen

@Composable
fun NavGraph(
    navController: NavHostController,
    startDestination: String = Screen.Login.route
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        // Auth screens
        composable(Screen.Login.route) {
            LoginScreen(
                onNavigateToRegister = {
                    navController.navigate(Screen.Register.route)
                },
                onLoginSuccess = {
                    navController.navigate(Screen.Wishlist.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Register.route) {
            RegisterScreen(
                onNavigateToLogin = {
                    navController.popBackStack()
                },
                onRegisterSuccess = {
                    navController.navigate(Screen.Wishlist.route) {
                        popUpTo(Screen.Register.route) { inclusive = true }
                    }
                }
            )
        }

        // Main screens
        composable(Screen.Wishlist.route) {
            WishlistScreen(
                onNavigateToAdd = {
                    navController.navigate(Screen.AddWishlist.route)
                },
                onNavigateToEdit = { itemId ->
                    navController.navigate(Screen.EditWishlist.createRoute(itemId))
                },
                onLogout = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Wishlist.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.AddWishlist.route) {
            AddEditWishlistScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onSaveSuccess = {
                    navController.popBackStack()
                }
            )
        }

        composable(
            Screen.EditWishlist.route,
            arguments = listOf(navArgument("itemId") { type = NavType.StringType })
        ) { backStackEntry ->
            val itemId = backStackEntry.arguments?.getString("itemId")
            AddEditWishlistScreen(
                itemId = itemId,
                onNavigateBack = {
                    navController.popBackStack()
                },
                onSaveSuccess = {
                    navController.popBackStack()
                }
            )
        }
    }
}