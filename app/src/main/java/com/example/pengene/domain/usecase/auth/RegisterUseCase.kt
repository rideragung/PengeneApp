package com.example.pengene.domain.usecase.auth

import com.example.pengene.domain.model.User
import com.example.pengene.domain.repository.IAuthRepository
import javax.inject.Inject

class RegisterUseCase @Inject constructor(
    private val authRepository: IAuthRepository
) {
    suspend operator fun invoke(email: String, password: String): Result<User> {
        if (email.isBlank()) {
            return Result.failure(Exception("Email tidak boleh kosong"))
        }

        if (password.isBlank()) {
            return Result.failure(Exception("Password tidak boleh kosong"))
        }

        if (password.length < 6) {
            return Result.failure(Exception("Password minimal 6 karakter"))
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            return Result.failure(Exception("Format email tidak valid"))
        }

        return authRepository.register(email, password)
    }
}