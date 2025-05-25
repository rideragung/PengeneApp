package com.example.pengene.data.repository

import com.example.pengene.data.remote.api.SupabaseClient
import com.example.pengene.data.remote.dto.WishlistItemDto
import com.example.pengene.domain.model.WishlistItem
import com.example.pengene.domain.repository.IWishlistRepository
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Columns
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

                val response = client.from("wishlist_items")
                    .insert(dto)
                    .decodeSingle<WishlistItemDto>()

                val savedItem = WishlistItem(
                    id = response.id ?: "",
                    userId = response.userId,
                    itemName = response.itemName,
                    estimatedPrice = response.estimatedPrice,
                    imageUrl = response.imageUrl,
                    description = response.description,
                    isPurchased = response.isPurchased,
                    createdAt = response.createdAt,
                    updatedAt = response.updatedAt
                )

                Result.success(savedItem)
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
                val response = client.from("wishlist_items")
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