package com.example.pengene.domain.usecase.auth

import com.example.pengene.domain.model.User
import com.example.pengene.domain.repository.IAuthRepository
import javax.inject.Inject

class LoginUseCase @Inject constructor(
    private val authRepository: IAuthRepository
) {
    suspend operator fun invoke(email: String, password: String): Result<User> {
        if (email.isBlank()) {
            return Result.failure(Exception("Email tidak boleh kosong"))
        }

        if (password.isBlank()) {
            return Result.failure(Exception("Password tidak boleh kosong"))
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            return Result.failure(Exception("Format email tidak valid"))
        }

        return authRepository.login(email, password)
    }
}