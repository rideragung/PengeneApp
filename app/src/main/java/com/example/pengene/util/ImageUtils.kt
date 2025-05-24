package com.example.pengene.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.util.*

object ImageUtils {

    fun compressImage(context: Context, imageUri: Uri, quality: Int = 80): ByteArray? {
        return try {
            val inputStream: InputStream? = context.contentResolver.openInputStream(imageUri)
            val bitmap = BitmapFactory.decodeStream(inputStream)

            val outputStream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream)

            outputStream.toByteArray()
        } catch (e: Exception) {
            null
        }
    }

    fun generateImageFileName(): String {
        return "wishlist_${UUID.randomUUID()}_${System.currentTimeMillis()}.jpg"
    }

    fun isImageUri(uri: String?): Boolean {
        return uri?.let {
            it.startsWith("content://") ||
                    it.startsWith("file://") ||
                    it.startsWith("http://") ||
                    it.startsWith("https://")
        } ?: false
    }
}