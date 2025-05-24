package com.example.pengene.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Error
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun ErrorDialog(
    isVisible: Boolean,
    title: String = "Error",
    message: String,
    onDismiss: () -> Unit,
    onRetry: (() -> Unit)? = null
) {
    if (isVisible) {
        AlertDialog(
            onDismissRequest = onDismiss,
            icon = {
                Icon(
                    imageVector = Icons.Default.Error,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.error
                )
            },
            title = {
                Text(
                    text = title,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
            },
            text = {
                Text(
                    text = message,
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center
                )
            },
            confirmButton = {
                if (onRetry != null) {
                    TextButton(onClick = onRetry) {
                        Text("Coba Lagi")
                    }
                } else {
                    TextButton(onClick = onDismiss) {
                        Text("OK")
                    }
                }
            },
            dismissButton = if (onRetry != null) {
                {
                    TextButton(onClick = onDismiss) {
                        Text("Batal")
                    }
                }
            } else null
        )
    }
}

@Preview
@Composable
fun ErrorDialogPreview() {
    MaterialTheme {
        ErrorDialog(
            isVisible = true,
            title = "Gagal Upload",
            message = "Terjadi kesalahan saat mengupload gambar. Silakan coba lagi.",
            onDismiss = {},
            onRetry = {}
        )
    }
}