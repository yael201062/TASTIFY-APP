package com.example.tastify.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tastify.data.database.AppDatabase
import com.example.tastify.data.model.Review
import com.example.tastify.data.repository.ReviewRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ReviewViewModel(private val repository: ReviewRepository) : ViewModel() {
    private val _reviews = MutableLiveData<List<Review>>()
    val reviews: LiveData<List<Review>> get() = _reviews

    fun getAllReviews() {
        viewModelScope.launch {
            _reviews.postValue(repository.getAllReviews())
        }
    }

    fun insertReview(review: Review) {
        viewModelScope.launch {
            repository.insertReview(review)
            getAllReviews() // טוען מחדש אחרי הוספת ביקורת
        }
    }
    fun loadReviews(restaurantId: String) {
        viewModelScope.launch {
            repository.getReviewsByRestaurant(restaurantId).collect { reviews ->
                _reviews.postValue(reviews)  // עדכון עם postValue
            }
        }
    }


}
