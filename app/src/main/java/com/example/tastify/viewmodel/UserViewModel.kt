package com.example.tastify.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tastify.data.model.User
import com.example.tastify.data.repository.UserRepository
import com.example.tastify.data.dao.UserDao
import kotlinx.coroutines.launch

class UserViewModel : ViewModel() {

    private val userRepository = com.example.tastify.data.dao.repository.UserRepository(UserDao())

    private val _currentUser = MutableLiveData<User?>()
    val currentUser: LiveData<User?> get() = _currentUser

    fun loadUser() {
        viewModelScope.launch {
            _currentUser.value = userRepository.getUser()
        }
    }

    fun updateUser(user: User) {
        viewModelScope.launch {
            userRepository.updateUser(user)
        }
    }

    init {
        loadUser()
    }
}
