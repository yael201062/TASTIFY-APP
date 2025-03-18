package com.example.tastify.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.example.tastify.data.repository.UserRepository
import kotlinx.coroutines.Dispatchers

class UserViewModel : ViewModel() {
    private val userRepository = UserRepository()

    val currentUser = liveData(Dispatchers.IO) {
        val user = userRepository.getCurrentUser()
        emit(user)
    }

    suspend fun updateProfile(name: String, imageUri: android.net.Uri?): String {
        val imageUrl = if (imageUri != null) userRepository.uploadProfileImage(imageUri) else ""
        userRepository.updateUserProfile(name, imageUrl)
        return imageUrl
    }
}
