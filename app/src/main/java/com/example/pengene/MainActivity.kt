package com.example.pengene

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.rememberNavController
import com.example.pengene.domain.model.AuthState
import com.example.pengene.ui.navigation.NavGraph
import com.example.pengene.ui.navigation.Screen
import com.example.pengene.ui.screens.auth.AuthViewModel
import com.example.pengene.ui.theme.WishlistVisualTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            WishlistVisualTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    val authViewModel: AuthViewModel = hiltViewModel()
                    val authState by authViewModel.authState.collectAsStateWithLifecycle()

                    LaunchedEffect(authState) {
                        when (authState) {
                            is AuthState.Authenticated -> {
                                navController.navigate(Screen.Wishlist.route) {
                                    popUpTo(Screen.Login.route) { inclusive = true }
                                }
                            }
                            is AuthState.Unauthenticated -> {
                                navController.navigate(Screen.Login.route) {
                                    popUpTo(Screen.Wishlist.route) { inclusive = true }
                                }
                            }
                            else -> {}
                        }
                    }

                    val startDestination = when (authState) {
                        is AuthState.Authenticated -> Screen.Wishlist.route
                        else -> Screen.Login.route
                    }

                    NavGraph(
                        navController = navController,
                        startDestination = startDestination
                    )
                }
            }
        }
    }
}