package com.example.pengene.domain.usecase.wishlist

import com.example.pengene.domain.repository.IStorageRepository
import java.util.UUID
import javax.inject.Inject

class UploadImageUseCase @Inject constructor(
    private val storageRepository: IStorageRepository
) {
    suspend operator fun invoke(imageUri: String): Result<String> {
        val fileName = "wishlist_${UUID.randomUUID()}.jpg"
        return storageRepository.uploadImage(imageUri, fileName)
    }
}