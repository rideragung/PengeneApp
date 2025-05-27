package com.example.pengene.ui.screens.wishlist

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.pengene.domain.model.AuthState
import com.example.pengene.domain.model.WishlistItem
import com.example.pengene.ui.components.WishlistItemCard
import com.example.pengene.ui.screens.auth.AuthViewModel
import com.example.pengene.ui.theme.WishlistVisualTheme


@Composable
fun WishlistScreen(
    onNavigateToAdd: () -> Unit,
    onNavigateToEdit: (String) -> Unit,
    onLogout: () -> Unit,
    wishlistViewModel: WishlistViewModel = hiltViewModel(),
    authViewModel: AuthViewModel = hiltViewModel()
) {
    val wishlistItems by wishlistViewModel.wishlistItems.collectAsStateWithLifecycle()
    val isLoading by wishlistViewModel.isLoading.collectAsStateWithLifecycle()
    val errorMessage by wishlistViewModel.errorMessage.collectAsStateWithLifecycle()
    val authState by authViewModel.authState.collectAsStateWithLifecycle()

    // Refresh wishlist items when screen is shown
    LaunchedEffect(Unit) {
        wishlistViewModel.loadWishlistItems()
    }
    
    // Pantau perubahan state autentikasi
    LaunchedEffect(authState) {
        if (authState == AuthState.Unauthenticated) {
            onLogout() // Ini akan memicu navigasi ke layar login
        }
    }

    WishlistScreenContent(
        wishlistItems = wishlistItems,
        isLoading = isLoading,
        errorMessage = errorMessage,
        onNavigateToAdd = onNavigateToAdd,
        onNavigateToEdit = onNavigateToEdit,
        onDeleteItem = { wishlistViewModel.deleteWishlistItem(it) },
        onTogglePurchased = { wishlistViewModel.togglePurchased(it) },
        onClearError = { wishlistViewModel.clearError() },
        onLogout = {
            authViewModel.logout() 
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun WishlistScreenContent(
    wishlistItems: List<WishlistItem>,
    isLoading: Boolean,
    errorMessage: String?,
    onNavigateToAdd: () -> Unit,
    onNavigateToEdit: (String) -> Unit,
    onDeleteItem: (String) -> Unit,
    onTogglePurchased: (WishlistItem) -> Unit,
    onClearError: () -> Unit,
    onLogout: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Wishlist Saya",
                        fontWeight = FontWeight.Bold
                    )
                },
                actions = {
                    IconButton(onClick = onLogout) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                            contentDescription = "Logout"
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNavigateToAdd
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Tambah Wishlist"
                )
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when {
                isLoading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                wishlistItems.isEmpty() -> {
                    Column(
                        modifier = Modifier.align(Alignment.Center),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Belum ada wishlist",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Medium
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Tambahkan barang impian Anda!",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(onClick = onNavigateToAdd) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Tambah Wishlist")
                        }
                    }
                }
                else -> {
                    // Sorting items: place unpurchased items at the top
                    val sortedItems = wishlistItems.sortedWith(compareBy { it.isPurchased })
                    
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(
                            items = sortedItems,
                            key = { it.id } // Using item.id as key for better animation
                        ) { item ->
                            WishlistItemCard(
                                item = item,
                                onEdit = { onNavigateToEdit(item.id) },
                                onDelete = { onDeleteItem(item.id) },
                                onTogglePurchased = { onTogglePurchased(item) }
                            )
                        }
                    }
                }
            }

            // Error handling
            errorMessage?.let { message ->
                Snackbar(
                    modifier = Modifier.padding(16.dp).align(Alignment.BottomCenter),
                    action = {
                        TextButton(onClick = onClearError) {
                            Text("Tutup")
                        }
                    },
                    containerColor = MaterialTheme.colorScheme.errorContainer,
                    contentColor = MaterialTheme.colorScheme.onErrorContainer
                ) {
                    Text(message)
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun WishlistScreenEmptyPreview() {
    WishlistVisualTheme {
        WishlistScreenContent(
            wishlistItems = emptyList(),
            isLoading = false,
            errorMessage = null,
            onNavigateToAdd = {},
            onNavigateToEdit = {},
            onDeleteItem = {},
            onTogglePurchased = {},
            onClearError = {},
            onLogout = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun WishlistScreenLoadingPreview() {
    WishlistVisualTheme {
        WishlistScreenContent(
            wishlistItems = emptyList(),
            isLoading = true,
            errorMessage = null,
            onNavigateToAdd = {},
            onNavigateToEdit = {},
            onDeleteItem = {},
            onTogglePurchased = {},
            onClearError = {},
            onLogout = {}
        )
    }
}