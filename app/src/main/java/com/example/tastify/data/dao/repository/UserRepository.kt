package com.example.tastify.data.dao.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.tastify.data.dao.UserDao
import com.example.tastify.data.model.User
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.await

class UserRepository(private val userDao: UserDao) {

    private val firestore = FirebaseFirestore.getInstance()
    private val usersCollection = firestore.collection("users")

    suspend fun insertUser(user: User) {
        withContext(Dispatchers.IO) {
            usersCollection.document(user.id).set(user).await()
            userDao.insertUser(user)
        }
    }

    suspend fun updateUser(user: User) {
        withContext(Dispatchers.IO) {
            usersCollection.document(user.id).set(user).await()
            userDao.updateUser(user)
        }
    }

    fun getUserById(userId: String): LiveData<User?> {
        val liveData = MutableLiveData<User?>()

        // מושך קודם מה-Firestore, אח"כ שומר ב-Room
        usersCollection.document(userId).get()
            .addOnSuccessListener { snapshot ->
                val user = snapshot.toObject(User::class.java)
                liveData.postValue(user)

                // שמור ל-ROOM כ-cache
                if (user != null) {
                    CoroutineScope(Dispatchers.IO).launch {
                        userDao.insertUser(user)
                    }
                }
            }
            .addOnFailureListener {
                // fallback ל-ROOM אם יש כשל ב-Firestore
                userDao.getUserById(userId).observeForever {
                    liveData.postValue(it)
                }
            }

        return liveData
    }

    fun getUserNameById(userId: String): LiveData<String?> {
        val nameLiveData = MutableLiveData<String?>()
        getUserById(userId).observeForever { user ->
            nameLiveData.postValue(user?.name)
        }
        return nameLiveData
    }

    suspend fun isUserExists(userId: String): Boolean {
        return withContext(Dispatchers.IO) {
            usersCollection.document(userId).get().await().exists()
        }
    }
}
