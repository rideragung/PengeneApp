package com.example.pengene.domain.usecase.wishlist

import com.example.pengene.domain.repository.IWishlistRepository
import javax.inject.Inject

class DeleteWishlistItemUseCase @Inject constructor(
    private val wishlistRepository: IWishlistRepository
) {
    suspend operator fun invoke(itemId: String): Result<Unit> {
        return wishlistRepository.deleteWishlistItem(itemId)
    }
}