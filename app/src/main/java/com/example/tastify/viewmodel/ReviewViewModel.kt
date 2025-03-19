package com.example.tastify.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tastify.data.model.Review
import com.example.tastify.data.repository.ReviewRepository
import kotlinx.coroutines.launch

class ReviewViewModel(private val repository: ReviewRepository) : ViewModel() {

    fun insertReview(review: Review) {
        viewModelScope.launch {
            repository.insertReview(review)
        }
    }

    fun getAllReviews(onResult: (List<Review>) -> Unit) {
        viewModelScope.launch {
            val reviews = repository.getAllReviews()
            onResult(reviews)
        }
    }
}
