package com.example.tastify.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tastify.data.AuthRepository
import kotlinx.coroutines.launch

class AuthViewModel(private val authRepository: AuthRepository) : ViewModel() {

    fun register(email: String, password: String, onResult: (Boolean, String?) -> Unit) {
        viewModelScope.launch {
            val result = authRepository.registerUser(email, password)
            onResult(result.isSuccess, result.exceptionOrNull()?.message)
        }
    }

    fun login(email: String, password: String, onResult: (Boolean, String?) -> Unit) {
        viewModelScope.launch {
            val result = authRepository.loginUser(email, password)
            onResult(result.isSuccess, result.exceptionOrNull()?.message)
        }
    }

    fun logout() {
        authRepository.logout()
    }
}