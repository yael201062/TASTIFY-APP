package com.example.tastify.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.firebase.Timestamp

@Entity(tableName = "reviews")
data class Review(
    @PrimaryKey val id: String = "",
    val userId: String = "",
    val restaurantId: String = "",
    val rating: Float = 0.0f,
    val comment: String = "",
    val imagePath: String? = null,
    val timestamp: Timestamp = Timestamp.now()
)
