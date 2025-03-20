package com.example.tastify.data.dao.repository

import com.example.tastify.data.dao.UserDao
import com.example.tastify.data.model.User

class UserRepository(private val userDao: UserDao) {

    suspend fun getUser(): User? {
        return userDao.getUser()
    }

    suspend fun updateUser(user: User) {
        userDao.updateUser(user)
    }
}
