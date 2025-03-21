package com.example.tastify.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tastify.data.model.Review
import com.example.tastify.data.dao.repository.ReviewRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ReviewViewModel(private val repository: ReviewRepository) : ViewModel() {

    private val _reviews = MutableStateFlow<List<Review>>(emptyList())
    val reviews: StateFlow<List<Review>> get() = _reviews

    init {
        loadAllReviews()
    }

    fun loadAllReviews() {
        viewModelScope.launch {
            repository.getAllReviews().collect { reviewList ->
                _reviews.value = reviewList
            }
        }
    }

    fun addReview(review: Review) {
        viewModelScope.launch {
            repository.insertReview(review)
            loadAllReviews() // טוען מחדש את כל הביקורות כדי לעדכן את הרשימה במסך הבית
        }
    }

    fun getReviewsByUser(userId: String): LiveData<List<Review>> {
        return repository.getReviewsByUser(userId)
    }

    fun getReviewsByRestaurant(restaurantId: String): Flow<List<Review>> {
        return repository.getReviewsByRestaurant(restaurantId)
    }
}
