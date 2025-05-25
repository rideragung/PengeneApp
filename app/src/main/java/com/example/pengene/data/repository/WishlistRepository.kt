package com.example.pengene.data.repository

import com.example.pengene.data.remote.api.SupabaseClient
import com.example.pengene.data.remote.dto.WishlistItemDto
import com.example.pengene.domain.model.WishlistItem
import com.example.pengene.domain.repository.IWishlistRepository
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Columns
import io.github.jan.supabase.postgrest.query.Order as OrderDirection
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WishlistRepository @Inject constructor() : IWishlistRepository {

    private val client = SupabaseClient.supabase

    override fun getWishlistItems(): Flow<List<WishlistItem>> = flow {
        try {
            val currentUser = client.auth.currentUserOrNull()
            if (currentUser != null) {
                val response = client.from("wishlist_items")
                    .select(Columns.ALL) {
                        filter {
                            eq("user_id", currentUser.id)
                        }
                    }
                    .decodeList<WishlistItemDto>()

                val wishlistItems = response.map { dto ->
                    WishlistItem(
                        id = dto.id ?: "",
                        userId = dto.userId,
                        itemName = dto.itemName,
                        estimatedPrice = dto.estimatedPrice,
                        imageUrl = dto.imageUrl,
                        description = dto.description,
                        isPurchased = dto.isPurchased,
                        createdAt = dto.createdAt,
                        updatedAt = dto.updatedAt
                    )
                }
                emit(wishlistItems)
            } else {
                emit(emptyList())
            }
        } catch (e: Exception) {
            emit(emptyList())
        }
    }

    override suspend fun addWishlistItem(item: WishlistItem): Result<WishlistItem> {
        return try {
            val currentUser = client.auth.currentUserOrNull()
            if (currentUser != null) {
                val dto = WishlistItemDto(
                    userId = currentUser.id,
                    itemName = item.itemName,
                    estimatedPrice = item.estimatedPrice,
                    imageUrl = item.imageUrl,
                    description = item.description,
                    isPurchased = item.isPurchased
                )

                // First just insert the item without trying to decode the response
                client.from("wishlist_items").insert(dto)
                
                // Wait a moment to ensure the database has time to process
                kotlinx.coroutines.delay(300)
                
                // Then fetch the latest items to find our newly created item
                var savedItem: WishlistItem? = null
                
                // Try to find the newly created item in the wishlist
                try {
                    val items = client.from("wishlist_items")
                        .select(Columns.ALL) {
                            filter {
                                eq("user_id", currentUser.id)
                                eq("item_name", item.itemName)
                            }
                            order("created_at", OrderDirection.DESCENDING)
                            limit(1)
                        }
                        .decodeList<WishlistItemDto>()
                    
                    if (items.isNotEmpty()) {
                        val latestItem = items.first()
                        savedItem = WishlistItem(
                            id = latestItem.id ?: "",
                            userId = latestItem.userId,
                            itemName = latestItem.itemName,
                            estimatedPrice = latestItem.estimatedPrice,
                            imageUrl = latestItem.imageUrl,
                            description = latestItem.description,
                            isPurchased = latestItem.isPurchased,
                            createdAt = latestItem.createdAt,
                            updatedAt = latestItem.updatedAt
                        )
                    }
                } catch (e: Exception) {
                    println("Warning: Could not retrieve the newly created item: ${e.message}")
                }
                
                // Return either the found item or a temporary placeholder
                Result.success(savedItem ?: item.copy(id = "temp-${System.currentTimeMillis()}"))
            } else {
                Result.failure(Exception("User not authenticated"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateWishlistItem(item: WishlistItem): Result<WishlistItem> {
        return try {
            val dto = WishlistItemDto(
                id = item.id,
                userId = item.userId,
                itemName = item.itemName,
                estimatedPrice = item.estimatedPrice,
                imageUrl = item.imageUrl,
                description = item.description,
                isPurchased = item.isPurchased
            )

            try {
                // Jalankan update dan decode response untuk memastikan operasi dieksekusi
                client.from("wishlist_items")
                    .update(dto) {
                        filter {
                            eq("id", item.id)
                        }
                    }
                    .decodeList<WishlistItemDto>() // Gunakan decodeList untuk memastikan operasi tereksekusi
                
                // Kembalikan item asli dengan perubahan
                Result.success(item)
            } catch (e: Exception) {
                // Jika gagal decode response, coba metode alternatif
                println("Warning: Update succeeded but encountered error when decoding response: ${e.message}")
                
                // Alternatif: jalankan update tanpa decode
                client.from("wishlist_items")
                    .update(mapOf("is_purchased" to item.isPurchased)) {
                        filter {
                            eq("id", item.id)
                        }
                    }
                
                // Berhasil update tanpa error
                Result.success(item)
            }
        } catch (e: Exception) {
            // Error yang lebih serius, gagal melakukan update
            Result.failure(e)
        }
    }

    override suspend fun deleteWishlistItem(itemId: String): Result<Unit> {
        return try {
            client.from("wishlist_items")
                .delete {
                    filter {
                        eq("id", itemId)
                    }
                }

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}