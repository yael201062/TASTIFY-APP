package com.example.tastify.data.dao

import com.example.tastify.data.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import kotlinx.coroutines.tasks.await

class UserDao {
    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    suspend fun getUser(): User? {
        val userId = auth.currentUser?.uid ?: return null
        return try {
            val document = firestore.collection("users").document(userId).get().await()
            document.toObject<User>()
        } catch (e: Exception) {
            null
        }
    }

    suspend fun updateUser(user: User) {
        val userId = auth.currentUser?.uid ?: return
        firestore.collection("users").document(userId).set(user).await()
    }
}
