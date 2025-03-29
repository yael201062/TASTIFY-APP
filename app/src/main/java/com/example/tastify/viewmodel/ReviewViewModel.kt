package com.example.tastify.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tastify.data.dao.repository.ReviewRepository
import com.example.tastify.data.model.Review
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ReviewViewModel(private val repository: ReviewRepository) : ViewModel() {

    private val _allReviews = MutableStateFlow<List<Review>>(emptyList())
    val allReviews: StateFlow<List<Review>> get() = _allReviews

    private val _userReviews = MutableStateFlow<List<Review>>(emptyList())
    val userReviews: StateFlow<List<Review>> get() = _userReviews

    fun loadAllReviews() {
        viewModelScope.launch {
            repository.getAllReviews().collect { result ->
                _allReviews.value = result
            }
        }
    }

    fun getReviewsByRestaurant(restaurantId: String) {
        viewModelScope.launch {
            repository.getReviewsByRestaurant(restaurantId).collect { filtered ->
                _allReviews.value = filtered
            }
        }
    }

    fun getReviewsByUser(userId: String) {
        viewModelScope.launch {
            repository.getReviewsByUser(userId).collect { reviews ->
                _userReviews.value = reviews
            }
        }
    }

    fun getReviewById(reviewId: String, onResult: (Review?) -> Unit) {
        viewModelScope.launch {
            val review = repository.getReviewById(reviewId)
            onResult(review)
        }
    }

    fun addReview(review: Review) {
        viewModelScope.launch {
            repository.insertReview(review)
        }
    }

    fun updateReview(review: Review) {
        viewModelScope.launch {
            repository.updateReview(review)
        }
    }

    fun deleteReview(review: Review) {
        viewModelScope.launch {
            repository.deleteReview(review)
        }
    }
}
