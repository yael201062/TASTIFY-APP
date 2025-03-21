package com.example.tastify.data.dao.repository

import androidx.lifecycle.LiveData
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


        fun getReviewsByUser(userId: String): LiveData<List<Review>> {
            return reviewDao.getReviewsByUserId(userId)
        }



    suspend fun insertReview(review: Review) {
        reviewDao.insertReview(review)
    }
}
