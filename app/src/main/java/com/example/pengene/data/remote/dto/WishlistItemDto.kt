package com.example.pengene.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class WishlistItemDto(
    val id: String? = null,
    @SerialName("user_id")
    val userId: String,
    @SerialName("item_name")
    val itemName: String,
    @SerialName("estimated_price")
    val estimatedPrice: Int? = null,
    @SerialName("image_url")
    val imageUrl: String? = null,
    val description: String? = null,
    @SerialName("is_purchased")
    val isPurchased: Boolean = false,
    @SerialName("created_at")
    val createdAt: String? = null,
    @SerialName("updated_at")
    val updatedAt: String? = null
)