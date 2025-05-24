package com.example.pengene.domain.usecase.wishlist

import com.example.pengene.domain.model.WishlistItem
import com.example.pengene.domain.repository.IWishlistRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetWishlistItemsUseCase @Inject constructor(
    private val wishlistRepository: IWishlistRepository
) {
    operator fun invoke(): Flow<List<WishlistItem>> {
        return wishlistRepository.getWishlistItems()
    }
}
