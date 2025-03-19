package com.example.tastify.data.dao

import androidx.room.*
import com.example.tastify.data.model.Review
import kotlinx.coroutines.flow.Flow

@Dao
interface ReviewDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReview(review: Review)

    @Query("SELECT * FROM reviews ORDER BY id DESC")
    suspend fun getAllReviews(): List<Review>

    @Query("SELECT * FROM reviews WHERE restaurantId = :restaurantId")
    fun getReviewsByRestaurant(restaurantId: String): Flow<List<Review>>

    @Delete
    suspend fun deleteReview(review: Review)
}
