package com.example.tastify.viewmodel

import androidx.lifecycle.*
import com.example.tastify.data.model.User
import com.example.tastify.data.dao.repository.UserRepository
import kotlinx.coroutines.launch
import com.google.firebase.auth.FirebaseAuth

class UserViewModel(private val repository: UserRepository) : ViewModel() {

    private val _currentUser = MutableLiveData<User?>()
    val currentUser: LiveData<User?> get() = _currentUser

    fun getUserName(userId: String): LiveData<String?> {
        return repository.getUserNameById(userId)
    }

    fun loadUser(userId: String) {
        repository.getUserById(userId).observeForever { user ->
            if (user == null) {
                val firebaseUser = FirebaseAuth.getInstance().currentUser
                if (firebaseUser != null) {
                    val newUser = User(
                        id = firebaseUser.uid,
                        name = firebaseUser.displayName ?: "משתמש חדש",
                        email = firebaseUser.email ?: "",
                        profileImageUrl = firebaseUser.photoUrl?.toString() ?: ""
                    )
                    viewModelScope.launch {
                        repository.insertUser(newUser)
                        _currentUser.postValue(newUser) // שמירה וטעינה מיידית
                    }
                }
            } else {
                _currentUser.postValue(user)
            }
        }
    }


    fun updateUser(user: User) {
        viewModelScope.launch {
            repository.updateUser(user)
            _currentUser.postValue(user)
        }
    }

    fun insertUser(user: User) {
        viewModelScope.launch {
            repository.insertUser(user)
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
