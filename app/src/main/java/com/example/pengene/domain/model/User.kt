package com.example.pengene.domain.model

data class User(
    val id: String,
    val email: String,
    val createdAt: String? = null
)