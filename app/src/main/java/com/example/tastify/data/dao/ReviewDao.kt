package com.example.tastify.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.tastify.data.model.Review
import kotlinx.coroutines.flow.Flow

@Dao
interface ReviewDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReview(review: Review)

    @Query("SELECT * FROM reviews ORDER BY timestamp DESC")
    fun getAllReviews(): Flow<List<Review>>

    @Query("SELECT * FROM reviews WHERE restaurantId = :restaurantId")
    fun getReviewsByRestaurant(restaurantId: String): Flow<List<Review>>

    @Query("SELECT * FROM reviews WHERE userId = :userId")
    fun getReviewsByUserId(userId: String): LiveData<List<Review>>

    @Query("SELECT * FROM reviews WHERE id = :reviewId")
    fun getReviewById(reviewId: String): LiveData<Review?>

    @Update
    suspend fun updateReview(review: Review)

    @Delete
    suspend fun deleteReview(review: Review)
}
