package com.example.tastify.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class RestaurantViewModel : ViewModel() {

    private val _restaurants = MutableLiveData<List<String>>()
    val restaurants: LiveData<List<String>> get() = _restaurants

    fun searchRestaurants(query: String) {
        Log.d("RestaurantViewModel", "Searching for restaurants with query: $query")
    }
}
