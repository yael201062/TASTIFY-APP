package com.example.tastify.data.dao.repository
import androidx.lifecycle.LiveData
import com.example.tastify.data.dao.UserDao
import com.example.tastify.data.model.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class UserRepository(private val userDao: UserDao) {

    suspend fun insertUser(user: User) {
        withContext(Dispatchers.IO) {
            userDao.insertUser(user)
        }
    }

    fun getUserById(userId: String): LiveData<User?> {
        return userDao.getUserById(userId)
    }

    suspend fun updateUser(user: User) {
        withContext(Dispatchers.IO) {
            userDao.updateUser(user)
        }
    }
}
