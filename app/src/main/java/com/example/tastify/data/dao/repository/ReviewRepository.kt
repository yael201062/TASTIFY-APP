package com.example.tastify.data.dao.repository

import androidx.lifecycle.LiveData
import com.example.tastify.data.dao.ReviewDao
import com.example.tastify.data.model.Review
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class ReviewRepository(private val reviewDao: ReviewDao) {

    fun getAllReviews(): Flow<List<Review>> {
        return reviewDao.getAllReviews()
    }

    fun getReviewsByRestaurant(restaurantId: String): Flow<List<Review>> {
        return reviewDao.getReviewsByRestaurant(restaurantId)
    }


        fun getReviewsByUser(userId: String): LiveData<List<Review>> {
            return reviewDao.getReviewsByUserId(userId)
        }

    fun getReviewById(reviewId: String): LiveData<Review?> {
        return reviewDao.getReviewById(reviewId)
    }

    suspend fun updateReview(review: Review) {
        withContext(Dispatchers.IO) {
            reviewDao.updateReview(review)
        }
    }

    suspend fun insertReview(review: Review) {
        reviewDao.insertReview(review)
    }

    suspend fun deleteReview(review: Review) {
        withContext(Dispatchers.IO) {
            reviewDao.deleteReview(review)
        }
    }
}
