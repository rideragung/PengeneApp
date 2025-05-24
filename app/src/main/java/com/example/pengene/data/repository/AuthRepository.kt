package com.example.pengene.data.repository

import com.example.pengene.data.remote.api.SupabaseClient
import com.example.pengene.domain.model.User
import com.example.pengene.domain.repository.IAuthRepository
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepository @Inject constructor() : IAuthRepository {

    private val auth = SupabaseClient.supabase.auth

    override suspend fun login(email: String, password: String): Result<User> {
        return try {
            auth.signInWith(Email) {
                this.email = email
                this.password = password
            }

            val currentUser = auth.currentUserOrNull()
            if (currentUser != null) {
                Result.success(
                    User(
                        id = currentUser.id,
                        email = currentUser.email ?: "",
                        createdAt = currentUser.createdAt.toString()
                    )
                )
            } else {
                Result.failure(Exception("Login failed"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun register(email: String, password: String): Result<User> {
        return try {
            auth.signUpWith(Email) {
                this.email = email
                this.password = password
            }

            val currentUser = auth.currentUserOrNull()
            if (currentUser != null) {
                Result.success(
                    User(
                        id = currentUser.id,
                        email = currentUser.email ?: "",
                        createdAt = currentUser.createdAt.toString()
                    )
                )
            } else {
                Result.failure(Exception("Registration failed"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun logout(): Result<Unit> {
        return try {
            auth.signOut()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getCurrentUser(): User? {
        return try {
            val currentUser = auth.currentUserOrNull()
            currentUser?.let {
                User(
                    id = it.id,
                    email = it.email ?: "",
                    createdAt = it.createdAt.toString()
                )
            }
        } catch (e: Exception) {
            null
        }
    }

    override suspend fun isUserLoggedIn(): Boolean {
        return auth.currentUserOrNull() != null
    }
}