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
                    .select(Columns.ALL)
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

            val response = client.from("wishlist_items")
                .update(dto) {
                    filter {
                        eq("id", item.id)
                    }
                }
                .decodeSingle<WishlistItemDto>()

            val updatedItem = WishlistItem(
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

            Result.success(updatedItem)
        } catch (e: Exception) {
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