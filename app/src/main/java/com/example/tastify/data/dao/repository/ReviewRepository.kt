package com.example.tastify.data.repository

import com.example.tastify.data.dao.ReviewDao
import com.example.tastify.data.model.Review
import kotlinx.coroutines.flow.Flow

class ReviewRepository(private val reviewDao: ReviewDao) {
    suspend fun insertReview(review: Review) = reviewDao.insertReview(review)
    suspend fun getAllReviews(): List<Review> = reviewDao.getAllReviews()
    suspend fun deleteReview(review: Review) = reviewDao.deleteReview(review)
    fun getReviewsByRestaurant(restaurantId: String): Flow<List<Review>> {
        return reviewDao.getReviewsByRestaurant(restaurantId)
    }
}
