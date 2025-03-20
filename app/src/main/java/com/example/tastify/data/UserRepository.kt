package com.example.tastify.data.repository

import com.example.tastify.data.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await

class UserRepository {
    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()
    private val storage = FirebaseStorage.getInstance()

    suspend fun getCurrentUser(): User? {
        val userId = auth.currentUser?.uid ?: return null
        val doc = firestore.collection("users").document(userId).get().await()
        return doc.toObject(User::class.java)
    }

    suspend fun updateUserProfile(name: String, imageUrl: String) {
        val userId = auth.currentUser?.uid ?: return
        firestore.collection("users").document(userId).update(
            mapOf("name" to name, "profileImageUrl" to imageUrl)
        ).await()
    }

    suspend fun uploadProfileImage(imageUri: android.net.Uri): String {
        val userId = auth.currentUser?.uid ?: return ""
        val ref = storage.reference.child("profile_images/$userId.jpg")
        ref.putFile(imageUri).await()
        return ref.downloadUrl.await().toString()
    }
}
