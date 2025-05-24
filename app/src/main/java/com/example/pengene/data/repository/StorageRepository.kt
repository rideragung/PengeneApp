package com.example.pengene.data.repository

import com.example.pengene.data.remote.api.SupabaseClient
import com.example.pengene.domain.repository.IStorageRepository
import io.github.jan.supabase.storage.storage
import io.github.jan.supabase.storage.upload
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StorageRepository @Inject constructor() : IStorageRepository {

    private val storage = SupabaseClient.supabase.storage

    override suspend fun uploadImage(imageUri: String, fileName: String): Result<String> {
        return try {
            val bucket = storage.from("wishlist-images")

            // Convert URI to file
            val file = File(imageUri)
            val fileBytes = file.readBytes()

            // Upload file
            bucket.upload(fileName, fileBytes)

            // Get public URL
            val publicUrl = bucket.publicUrl(fileName)

            Result.success(publicUrl)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deleteImage(imageUrl: String): Result<Unit> {
        return try {
            // Extract filename from URL
            val fileName = imageUrl.substringAfterLast("/")

            val bucket = storage.from("wishlist-images")
            bucket.delete(fileName)

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}