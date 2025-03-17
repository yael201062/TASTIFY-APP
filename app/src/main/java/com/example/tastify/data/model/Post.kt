package com.example.tastify.data.model

data class Post(
    val id: String = "",
    val userId: String = "",
    val userName: String = "",
    val content: String = "",
    val imageUrl: String? = null,
    val timestamp: Long = System.currentTimeMillis()
)