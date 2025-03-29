package com.example.tastify.viewmodel

import androidx.lifecycle.*
import com.example.tastify.data.dao.repository.UserRepository
import com.example.tastify.data.model.User
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class UserViewModel(private val repository: UserRepository) : ViewModel() {

    private val _currentUser = MediatorLiveData<User?>()
    val currentUser: LiveData<User?> get() = _currentUser

    private var userSource: LiveData<User?>? = null

    fun loadUser(userId: String) {
        userSource?.let { _currentUser.removeSource(it) } // הסר מקור קודם אם קיים

        val newSource = repository.getUserById(userId)
        userSource = newSource
        _currentUser.addSource(newSource) { user ->
            _currentUser.value = user
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
