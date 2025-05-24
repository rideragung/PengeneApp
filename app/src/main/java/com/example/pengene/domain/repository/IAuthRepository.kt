package com.example.pengene.domain.repository

import com.example.pengene.domain.model.User

interface IAuthRepository {
    suspend fun login(email: String, password: String): Result<User>
    suspend fun register(email: String, password: String): Result<User>
    suspend fun logout(): Result<Unit>
    suspend fun getCurrentUser(): User?
    suspend fun isUserLoggedIn(): Boolean
}