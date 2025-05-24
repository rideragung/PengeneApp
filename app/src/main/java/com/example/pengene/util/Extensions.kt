package com.example.pengene.util

import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

fun Double.toCurrencyString(): String {
    return NumberFormat.getCurrencyInstance(Locale("id", "ID")).format(this)
}

fun String.toDateString(): String {
    return try {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
        val outputFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
        val date = inputFormat.parse(this)
        outputFormat.format(date ?: Date())
    } catch (e: Exception) {
        this
    }
}

fun String.isValidEmail(): Boolean {
    return android.util.Patterns.EMAIL_ADDRESS.matcher(this).matches()
}

fun String.truncate(maxLength: Int): String {
    return if (length <= maxLength) this else substring(0, maxLength) + "..."
}