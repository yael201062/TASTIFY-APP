package com.example.tastify.data.dao

import androidx.room.*
import com.example.tastify.data.model.Review

@Dao
interface ReviewDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReview(review: Review)

    @Query("SELECT * FROM reviews ORDER BY id DESC")
    suspend fun getAllReviews(): List<Review>

    @Delete
    suspend fun deleteReview(review: Review)
}
