package com.example.tastify.data.model

data class Restaurant(
    val id: String = "",
    val name: String = "",
    val address: String = "",
    val cuisine: String = "",
    val rating: Float = 0.0f,
    val imageUrl: String = "",
    val menu: List<String> = emptyList()
)
