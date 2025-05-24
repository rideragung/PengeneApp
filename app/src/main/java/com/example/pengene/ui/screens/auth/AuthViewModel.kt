package com.example.pengene.ui.screens.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pengene.domain.model.AuthState
import com.example.pengene.domain.usecase.auth.GetCurrentUserUseCase
import com.example.pengene.domain.usecase.auth.LoginUseCase
import com.example.pengene.domain.usecase.auth.LogoutUseCase
import com.example.pengene.domain.usecase.auth.RegisterUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase,
    private val registerUseCase: RegisterUseCase,
    private val logoutUseCase: LogoutUseCase,
    private val getCurrentUserUseCase: GetCurrentUserUseCase
) : ViewModel() {

    private val _authState = MutableStateFlow<AuthState>(AuthState.Loading)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    private val _email = MutableStateFlow("")
    val email: StateFlow<String> = _email.asStateFlow()

    private val _password = MutableStateFlow("")
    val password: StateFlow<String> = _password.asStateFlow()

    init {
        checkAuthState()
    }

    fun setEmail(email: String) {
        _email.value = email.trim()
    }

    fun setPassword(password: String) {
        _password.value = password
    }

    fun login() {
        viewModelScope.launch {
            _authState.value = AuthState.Loading

            val result = loginUseCase(email.value, password.value)
            _authState.value = if (result.isSuccess) {
                AuthState.Authenticated
            } else {
                AuthState.Error(result.exceptionOrNull()?.message ?: "Login gagal")
            }
        }
    }

    fun register() {
        viewModelScope.launch {
            _authState.value = AuthState.Loading

            val result = registerUseCase(email.value, password.value)
            _authState.value = if (result.isSuccess) {
                AuthState.Authenticated
            } else {
                AuthState.Error(result.exceptionOrNull()?.message ?: "Registrasi gagal")
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            val result = logoutUseCase()
            _authState.value = if (result.isSuccess) {
                AuthState.Unauthenticated
            } else {
                AuthState.Error("Logout gagal")
            }
        }
    }

    private fun checkAuthState() {
        viewModelScope.launch {
            val currentUser = getCurrentUserUseCase()
            _authState.value = if (currentUser != null) {
                AuthState.Authenticated
            } else {
                AuthState.Unauthenticated
            }
        }
    }

    fun clearError() {
        if (_authState.value is AuthState.Error) {
            _authState.value = AuthState.Unauthenticated
        }
    }
}