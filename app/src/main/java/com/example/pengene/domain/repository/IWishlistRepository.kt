package com.example.pengene.domain.repository

import com.example.pengene.domain.model.WishlistItem
import kotlinx.coroutines.flow.Flow

interface IWishlistRepository {
    fun getWishlistItems(): Flow<List<WishlistItem>>
    suspend fun addWishlistItem(item: WishlistItem): Result<WishlistItem>
    suspend fun updateWishlistItem(item: WishlistItem): Result<WishlistItem>
    suspend fun deleteWishlistItem(itemId: String): Result<Unit>
}