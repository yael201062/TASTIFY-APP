package com.example.tastify.data.model

data class Review(
    val id: String = "",
    val userId: String = "",
    val restaurantId: String = "",
    val rating: Float = 0.0f,
    val comment: String = "",
    val timestamp: Long = System.currentTimeMillis()
)
