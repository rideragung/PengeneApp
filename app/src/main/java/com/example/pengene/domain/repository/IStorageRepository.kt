package com.example.pengene.domain.repository

interface IStorageRepository {
    suspend fun uploadImage(imageUri: String, fileName: String): Result<String>
    suspend fun deleteImage(imageUrl: String): Result<Unit>
}