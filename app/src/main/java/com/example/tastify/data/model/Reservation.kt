package com.example.tastify.data.model

data class Reservation(
    val id: String = "",
    val userId: String = "",
    val restaurantId: String = "",
    val dateTime: Long = System.currentTimeMillis(),
    val numberOfGuests: Int = 1,
    val status: String = "Pending"
)
