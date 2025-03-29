package com.example.tastify.viewmodel

import androidx.lifecycle.*
import com.example.tastify.data.dao.repository.UserRepository
import com.example.tastify.data.model.User
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class UserViewModel(private val repository: UserRepository) : ViewModel() {

    private val _currentUser = MutableLiveData<User?>()
    val currentUser: LiveData<User?> get() = _currentUser

    fun loadUser(userId: String) {
        repository.getUserById(userId).observeForever { user ->
            _currentUser.postValue(user)
        }
    }

    fun getUserById(userId: String): LiveData<User?> {
        return repository.getUserById(userId)
    }

    fun getUserName(userId: String): LiveData<String?> {
        return repository.getUserNameById(userId)
    }

    fun insertUser(user: User) {
        viewModelScope.launch {
            repository.insertUser(user)
            _currentUser.postValue(user)
        }
    }

    fun updateUser(user: User) {
        viewModelScope.launch {
            repository.updateUser(user)
            _currentUser.postValue(user)
        }
    }

    class Factory(private val repository: UserRepository) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(UserViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return UserViewModel(repository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
