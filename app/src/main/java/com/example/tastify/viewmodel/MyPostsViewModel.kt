package com.example.tastify.ui.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.tastify.data.model.Post
import com.google.firebase.firestore.FirebaseFirestore

class MyPostsViewModel : ViewModel() {

    private val _myPosts = MutableLiveData<List<Post>>()
    val myPosts: LiveData<List<Post>> get() = _myPosts

    fun getMyPosts(userId: String) {
        FirebaseFirestore.getInstance()
            .collection("posts")
            .whereEqualTo("userId", userId)
            .get()
            .addOnSuccessListener { result ->
                val posts = result.documents.mapNotNull { it.toObject(Post::class.java) }
                _myPosts.value = posts
            }
    }
}
