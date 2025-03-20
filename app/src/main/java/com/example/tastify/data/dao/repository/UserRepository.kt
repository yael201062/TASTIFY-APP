package com.example.tastify.data.repository

import androidx.lifecycle.LiveData
import com.example.tastify.data.dao.UserDao
import com.example.tastify.data.model.User

class UserRepository(private val userDao: UserDao) {

    fun getUser(userId: String): LiveData<User?> {
        return userDao.getUser(userId)
    }

    suspend fun insertUser(user: User) {
        userDao.insertUser(user)
    }

    suspend fun updateUser(user: User) {
        userDao.updateUser(user)
    }
}
