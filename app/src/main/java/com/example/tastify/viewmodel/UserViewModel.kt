package com.example.tastify.viewmodel

import android.app.Application
import androidx.lifecycle.*
import com.example.tastify.data.database.AppDatabase
import com.example.tastify.data.model.User
import com.example.tastify.data.repository.UserRepository
import kotlinx.coroutines.launch

class UserViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: UserRepository
    private val _currentUser = MutableLiveData<User?>()
    val currentUser: LiveData<User?> get() = _currentUser

    init {
        val userDao = AppDatabase.getDatabase(application).userDao()
        repository = UserRepository(userDao)
    }

    fun loadUser(userId: String) {
        repository.getUser(userId).observeForever { user ->
            _currentUser.value = user
        }
    }

    fun updateUser(user: User) {
        viewModelScope.launch {
            repository.updateUser(user)
            _currentUser.postValue(user)
        }
    }
}
