package com.example.tastify.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "reviews")
data class Review(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val userId: String = "",
    val restaurantId: String = "",
    val rating: Float = 0.0f,
    val comment: String = "",
    val imagePath: String? = null,
    val timestamp: Long = System.currentTimeMillis()
)



