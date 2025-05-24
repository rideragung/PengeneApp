package com.example.pengene.domain.usecase.wishlist

import com.example.pengene.domain.model.WishlistItem
import com.example.pengene.domain.repository.IWishlistRepository
import javax.inject.Inject

class AddWishlistItemUseCase @Inject constructor(
    private val wishlistRepository: IWishlistRepository
) {
    suspend operator fun invoke(item: WishlistItem): Result<WishlistItem> {
        if (item.itemName.isBlank()) {
            return Result.failure(Exception("Nama barang tidak boleh kosong"))
        }

        return wishlistRepository.addWishlistItem(item)
    }
}