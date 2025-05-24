package com.example.pengene.util

object Constants {
    // Supabase Configuration
    const val SUPABASE_URL = "YOUR_SUPABASE_URL"
    const val SUPABASE_ANON_KEY = "YOUR_SUPABASE_ANON_KEY"

    // Storage
    const val WISHLIST_IMAGES_BUCKET = "wishlist-images"

    // Validation
    const val MIN_PASSWORD_LENGTH = 6
    const val MAX_ITEM_NAME_LENGTH = 255
    const val MAX_DESCRIPTION_LENGTH = 1000

    // UI
    const val CARD_ELEVATION = 4
    const val CORNER_RADIUS = 12
    const val IMAGE_SIZE_SMALL = 80
    const val IMAGE_SIZE_MEDIUM = 200

    // Error Messages
    const val ERROR_NETWORK = "Periksa koneksi internet Anda"
    const val ERROR_GENERIC = "Terjadi kesalahan, silakan coba lagi"
    const val ERROR_INVALID_EMAIL = "Format email tidak valid"
    const val ERROR_WEAK_PASSWORD = "Password minimal 6 karakter"
    const val ERROR_EMPTY_FIELD = "Field ini tidak boleh kosong"
}