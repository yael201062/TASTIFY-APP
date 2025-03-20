package com.example.tastify.data.api

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface OpenTableApiService {

    @GET("restaurants")
    fun getRestaurantsByName(
        @Query("name") name: String
    ): Call<OpenTableResponse>

    @GET("restaurant")
    fun getRestaurantDetails(
        @Query("id") restaurantId: String
    ): Call<OpenTableRestaurant>
}
