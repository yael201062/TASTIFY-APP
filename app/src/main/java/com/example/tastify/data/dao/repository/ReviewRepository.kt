package com.example.tastify.data.dao.repository

import com.example.tastify.data.dao.ReviewDao
import com.example.tastify.data.model.Review
import kotlinx.coroutines.flow.Flow

class ReviewRepository(private val reviewDao: ReviewDao) {

    fun getAllReviews(): Flow<List<Review>> {
        return reviewDao.getAllReviews()
    }

    fun getReviewsByRestaurant(restaurantId: String): Flow<List<Review>> {
        return reviewDao.getReviewsByRestaurant(restaurantId)
    }

    suspend fun insertReview(review: Review) {
        reviewDao.insertReview(review)
    }
}
