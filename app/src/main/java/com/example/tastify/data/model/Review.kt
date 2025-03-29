package com.example.tastify.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.firebase.Timestamp

@Entity(tableName = "reviews")
data class Review(
    @PrimaryKey
    var id: String = "",
    var userId: String = "",
    var restaurantId: String = "",
    var rating: Float = 0.0f,
    var comment: String = "",
    var imagePath: String? = null,
    var timestamp: Timestamp = Timestamp.now()
)

