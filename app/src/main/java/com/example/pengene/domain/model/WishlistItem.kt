package com.example.pengene.domain.model

data class WishlistItem(
    val id: String = "",
    val userId: String = "",
    val itemName: String,
    val estimatedPrice: Double? = null,
    val imageUrl: String? = null,
    val description: String? = null,
    val isPurchased: Boolean = false,
    val createdAt: String? = null,
    val updatedAt: String? = null
)